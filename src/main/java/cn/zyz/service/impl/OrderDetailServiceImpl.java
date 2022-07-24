package cn.zyz.service.impl;

import cn.zyz.entity.OrderDetail;
import cn.zyz.mapper.OrderDetailMapper;
import cn.zyz.service.OrderDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @program: reggie
 * @author: zyz
 * @create: 2022-07-24 12:43
 **/

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}



