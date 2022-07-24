package cn.zyz.controller;

import cn.zyz.common.BaseContext;
import cn.zyz.common.R;
import cn.zyz.dto.DishDto;
import cn.zyz.entity.Dish;
import cn.zyz.entity.OrderDetail;
import cn.zyz.entity.Orders;
import cn.zyz.service.OrderDetailService;
import cn.zyz.service.OrdersService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @program: reggie
 * @author: zyz
 * @create: 2022-07-24 12:44
 **/

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;


    /**
     * 用户下单
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        ordersService.submit(orders);
        return R.success("下单成功");
    }


    /**
     * 用户界面订单信息分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> userPage(Integer page,Integer pageSize){

        //添加分页条件
        Page<Orders> pageInfo = new Page<>(page,pageSize);

        //条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件,当前用户订单
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        //添加排序条件,根据下单时间降序
        queryWrapper.orderByDesc(Orders::getOrderTime);

        //执行分页查询
        ordersService.page(pageInfo,queryWrapper);

        //拿到订单信息集合
        List<Orders> records = pageInfo.getRecords();

        //封装每个订单对应的订单详细信息
        records=records.stream().map(order -> {
            LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OrderDetail::getOrderId,order.getId());
            List<OrderDetail> list = orderDetailService.list(wrapper);
            order.setOrderDetails(list);
            return order;
        }).collect(Collectors.toList());

        //将放入订单详细信息的订单放回
        pageInfo.setRecords(records);

        return R.success(pageInfo);
    }


    /**
     * 再来一单
     * @param orders
     * @return
     */
    @PostMapping("/again")
    public R<String> again(@RequestBody Orders orders){
        //先根据传递过来的订单信息
        Orders oldOrders = ordersService.getById(orders.getId());
        //新生成一个orderId
        long newOrderId = IdWorker.getId();
        //将需要更新的属性更新
        oldOrders.setId(newOrderId);
        oldOrders.setNumber(String.valueOf(newOrderId));
        oldOrders.setStatus(2);
        LocalDateTime now = LocalDateTime.now();
        oldOrders.setOrderTime(now);
        oldOrders.setCheckoutTime(now);

        //将原订单对应的订单详细信息也更新
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId,orders.getId());
        List<OrderDetail> list = orderDetailService.list(queryWrapper);
        list=list.stream().map(orderDetail -> {
            //将新生成的订单ID赋值
            orderDetail.setOrderId(newOrderId);
            //新生成一个订单详细信息ID
            orderDetail.setId(IdWorker.getId());
            return orderDetail;
        }).collect(Collectors.toList());
        oldOrders.setOrderDetails(list);

        //新生成的订单信息保存
        ordersService.save(oldOrders);

        //新生成的订单详细信息保存
        orderDetailService.saveBatch(list);
        return R.success("下单成功");
    }


    /**
     * 后台管理界面订单信息分页查询
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page> pageLit(Integer page,Integer pageSize,String number,String beginTime,String endTime){
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.likeRight(!StringUtils.isEmpty(number),Orders::getNumber,number);
        if (beginTime!=null && endTime!=null){
            queryWrapper.between(Orders::getOrderTime,beginTime,endTime);
        }
        ordersService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }


    /**
     * 修改订单状态
     * 订单状态 1待付款，2待派送，3已派送，4已完成，5已取消
     * @param orders
     * @return
     */
    @PutMapping
    public R<String> paiSong(@RequestBody Orders orders){
        LambdaUpdateWrapper<Orders> queryWrapper = new LambdaUpdateWrapper();
        queryWrapper.eq(Orders::getId,orders.getId());
        queryWrapper.set(Orders::getStatus,orders.getStatus());
        ordersService.update(queryWrapper);
        return R.success("状态修改成功");
    }
}



