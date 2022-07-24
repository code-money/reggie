package cn.zyz.service.impl;

import cn.zyz.common.CustomException;
import cn.zyz.common.R;
import cn.zyz.dto.DishDto;
import cn.zyz.dto.SetmealDto;
import cn.zyz.entity.Setmeal;
import cn.zyz.entity.SetmealDish;
import cn.zyz.mapper.SetmealMapper;
import cn.zyz.service.SetmealDishService;
import cn.zyz.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * @program: reggie
 * @author: zyz
 * @create: 2022-07-21 11:31
 **/

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //保存基本字段到setmeal表中
        this.save(setmealDto);

        //获取套餐id
        Long setmealId = setmealDto.getId();

        //获取套餐包含的菜品信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        //通过stream流方式更新套餐包含的菜品信息
        setmealDishes = setmealDishes.stream().map(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
            return setmealDish;
        }).collect(Collectors.toList());

        //保存套餐和菜品的关联信息，操作setmeal_dish，执行insert操作
        setmealDishService.saveBatch(setmealDishes);

    }

    @Override
    public void removeWithDish(Long... ids) {

        /*
            select count(*) from setmeal where id in(1,2,3) and status = 1
            拼接该Sql语句，如果传递过来的套餐中有启售状态的套餐，那么抛出异常
         */
        //查询套餐状态，确定是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        //如果不能删除，抛出异常
        int count = this.count(queryWrapper);
        if (count>0){
            throw new CustomException("套餐正在售卖中,不能删除");
        }

        //删除本表数据--setmeal
        this.removeByIds(Arrays.asList(ids));

        //删除关联表中的数据--setmealDish
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(queryWrapper1);
    }

    @Override
    public SetmealDto getByIdWithDish(Long id) {
        Setmeal setmeal = this.getById(id);
        Long setmealId = setmeal.getId();

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealId);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);

        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);

        setmealDto.setSetmealDishes(list);
        return setmealDto;
    }

    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        //更新基本信息
        this.updateById(setmealDto);

        //清理套餐对应的菜品信息
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);

        //
        Long setmealId = setmealDto.getId();
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes=setmealDishes.stream().map(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
            return setmealDish;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }


}



