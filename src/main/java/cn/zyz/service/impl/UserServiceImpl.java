package cn.zyz.service.impl;

import cn.zyz.entity.User;
import cn.zyz.mapper.UserMapper;
import cn.zyz.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @program: reggie
 * @author: zyz
 * @create: 2022-07-22 19:32
 **/

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}



