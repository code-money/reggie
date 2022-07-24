package cn.zyz.service.impl;

import cn.zyz.entity.Employee;
import cn.zyz.mapper.EmployeeMapper;
import cn.zyz.service.EmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @program: reggie
 * @author: zyz
 * @create: 2022-07-17 16:05
 **/

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}



