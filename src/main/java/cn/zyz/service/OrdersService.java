package cn.zyz.service;

import cn.zyz.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

public interface OrdersService extends IService<Orders> {

    /**
     * 用户下单
     * @param orders
     */
    @Transactional
    void submit(Orders orders);
}
