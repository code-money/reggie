package cn.zyz.service.impl;

import cn.zyz.common.CustomException;
import cn.zyz.entity.Category;
import cn.zyz.entity.Dish;
import cn.zyz.entity.Setmeal;
import cn.zyz.mapper.CategoryMapper;
import cn.zyz.service.CategoryService;
import cn.zyz.service.DishService;
import cn.zyz.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: reggie
 * @author: zyz
 * @create: 2022-07-21 10:32
 **/

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;


    /**
     * 根据id删除分类，删除之前进行判断是否关联菜品|套餐
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int dishCount = dishService.count(dishLambdaQueryWrapper);
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int setmealCount = setmealService.count(setmealLambdaQueryWrapper);

        //查询当前分类是否已经关联菜品
        if (dishCount>0){
            //在调用出抛出异常
            throw new CustomException("已关联菜品，无法删除");
        }
        //查询当前分类是否已经关联套餐
        if (setmealCount>0){
            //在调用出抛出异常
            throw new CustomException("已关联套餐，无法删除");
        }

        //正常删除分类
        super.removeById(id);
    }
}



