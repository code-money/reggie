package cn.zyz.controller;

import cn.zyz.common.R;
import cn.zyz.entity.Employee;

import cn.zyz.service.EmployeeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpSession;

import java.time.LocalDateTime;


/**
 * @program: reggie
 * @author: zyz
 * @create: 2022-07-17 16:06
 **/

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;


    /**
     * 员工登录
     *
     * @param session
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpSession session, @RequestBody Employee employee) {
        log.info("接收到的employee信息:" + employee.toString());
        /**
         1、将页面提交的密码password进行md5加密处理
         2、根据页面提交的用户名username查询数据库
         3、如果没有查询到则返回登录失败结果
         4、密码比对，如果不一致则返回登录失败结果
         5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
         6、登录成功，将员工id存入Session并返回登录成功结果
         */
        //1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        log.info("加密后的密码:" + password);

        //2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3、如果没有查询到则返回登录失败结果
        if (emp == null) {
            return R.error("登陆失败");
        }

        //4、密码比对，如果不一致则返回登录失败结果
        if (!emp.getPassword().equals(password)) {
            return R.error("登陆失败");
        }

        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }

        //6、登录成功，将员工id存入Session并返回登录成功结果
        session.setAttribute("employee", emp.getId());
        return R.success(emp);
    }


    /**
     * 退出登录
     *
     * @param session
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpSession session) {
        //清理session中保存的当前的登陆员工
        session.removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpSession session, @RequestBody Employee employee) {
        log.info("新增员工，员工信息:{}", employee.toString());
        //对密码进行MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        employeeService.save(employee);

        return R.success("添加成功");
    }


    /**
     * 员工信息分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> pageQuery(Integer page, Integer pageSize, String name) {
        //构造分页查询器
        Page pageInfo = new Page(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件,模糊查询
        queryWrapper.like(!StringUtils.isEmpty(name), Employee::getName, name);
        //添加排序条件,根据修改时间排序
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询，查询出来的数据会封装到pageInfo中
        employeeService.page(pageInfo, queryWrapper);
        //将分页信息返回给前端
        return R.success(pageInfo);
    }


    /**
     * 修改员工信息
     * @param session
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpSession session,@RequestBody Employee employee){
        log.info("雇员信息:{}",employee.toString());

        employeeService.updateById(employee);

        return R.success("员工信息修改成功");
    }

    /**
     * 根据ID查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("需要修改的雇员ID:{}",id);
        Employee byId = employeeService.getById(id);
        return R.success(byId);
    }

}



