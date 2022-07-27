package cn.zyz.controller;

import cn.zyz.common.R;
import cn.zyz.entity.User;
import cn.zyz.service.UserService;
import cn.zyz.utils.ValidateCodeUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @program: reggie
 * @author: zyz
 * @create: 2022-07-22 19:33
 **/

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;


    /**
     * 发送验证码
     * @param session
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMessage(HttpSession session, @RequestBody User user){
        //获取手机号
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)){
            //生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}",code);

            //将生成的验证码放到session中
            //session.setAttribute(phone,code);

            //将生成的验证码缓存到redis中，并设置有效期5分钟
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);

            return R.success("验证码发送成功");
        }
        return R.error("短信发送失败");
    }


    /**
     * 移动端用户登陆
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());
        //获取手机号
        String phone =(String) map.get("phone");
        //获取验证码
        String code =(String) map.get("code");

        //从Sesison中获取保存的验证码
//        String codeInSession =(String) session.getAttribute(phone);

        //从redis中取出验证码
        String codeInSession  =(String) redisTemplate.opsForValue().get(phone);

        //进行验证码的对比
        if (codeInSession!=null && code.equals(codeInSession)){
            //如果对比成功，说明登陆成功

            //判断当前是否为新用户，如果为新用户则自动完成注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            //根据手机号查找用户,如果为空说明数据库中没有改手机号信息，用户为新用户
            if (user==null){
                //如果为新用户那么录入数据并保存到数据库中
                user=new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            //将用户放到session中
            session.setAttribute("user",user.getId());
            //如果登陆成功，删除redis缓存中的验证码
            redisTemplate.delete(phone);

            return R.success(user);
        }
        return R.error("登陆失败");
    }


    @PostMapping("/loginout")
    public R<String> logout(HttpSession session){
        log.info("用户登出");
        session.removeAttribute("user");
        return R.success("用户退出");
    }


}



