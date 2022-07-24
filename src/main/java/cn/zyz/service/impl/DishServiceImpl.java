package cn.zyz.service.impl;

import cn.zyz.common.CustomException;
import cn.zyz.dto.DishDto;
import cn.zyz.entity.Dish;
import cn.zyz.entity.DishFlavor;
import cn.zyz.mapper.DishMapper;
import cn.zyz.service.DishFlavorService;
import cn.zyz.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: reggie
 * @author: zyz
 * @create: 2022-07-21 11:29
 **/

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {


    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish
        /*
            前端拿到的dishDto第一时间Id是没有值的
            在执行完save()方法后，id会根据MP的主键生成策略生成，MP会将生成的id放到该对象中
            所以在执行Long dishId = dishDto.getId()时，可以获取到Id
         */
        this.save(dishDto);
        //获取菜品ID
        Long dishId = dishDto.getId();
        //获取口味集合
        List<DishFlavor> flavors = dishDto.getFlavors();
        //通过stream流方式给口味集合的dishId赋值
        flavors = flavors.stream().map(dishFlavor -> {
            dishFlavor.setDishId(dishId);
            return dishFlavor;
        }).collect(Collectors.toList());

        //保存菜品的口味信息到dish_flavor
        dishFlavorService.saveBatch(flavors);
    }


    @Override
    public void removeWithFlavor(Long... ids) {

        /*
            查询菜品状态，确定是否可以删除
            select count(*) from dish where id in(1,2,3) and status = 1
            拼接该Sql语句，如果传递过来的id对应的菜品中有启售状态的菜品，那么抛出异常
         */
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId,ids);
        queryWrapper.eq(Dish::getStatus,1);

        //count>0，有菜品状态为1，售卖中，不能删除
        int count = this.count(queryWrapper);
        if (count>0){
            //抛出自定义异常,throw之后的语句不会执行
            throw new CustomException("菜品正在售卖中，不能删除");
        }

        //删除菜品信息
        this.removeByIds(Arrays.asList(ids));

        //删除口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(queryWrapper1);

    }


    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品信息
        Dish dish = this.getById(id);

        //查询口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);

        //拷贝dish到dishDto并设置dishDto的Flavors(口味)属性
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        dishDto.setFlavors(list);
        return dishDto;
    }

    /**
     * 更新菜品信息
     * @param dishDto
     */
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);

        //清理当前菜品对应口味数据
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //添加当前提交过来的口味数据
        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        //通过stream流方式给口味集合的dishId赋值
        flavors = flavors.stream().map(dishFlavor -> {
            dishFlavor.setDishId(dishId);
            return dishFlavor;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

}



