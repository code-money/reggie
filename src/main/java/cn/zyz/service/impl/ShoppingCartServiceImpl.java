package cn.zyz.service.impl;

import cn.zyz.entity.ShoppingCart;
import cn.zyz.mapper.ShoppingCartMapper;
import cn.zyz.service.ShoppingCartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @program: reggie
 * @author: zyz
 * @create: 2022-07-23 16:49
 **/

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart>  implements ShoppingCartService {
}



