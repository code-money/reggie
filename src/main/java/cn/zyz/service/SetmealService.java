package cn.zyz.service;

import cn.zyz.dto.DishDto;
import cn.zyz.dto.SetmealDto;
import cn.zyz.entity.Dish;
import cn.zyz.entity.Setmeal;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;


public interface SetmealService extends IService<Setmeal> {

    @Transactional
    //新增套餐，同时插入套餐对应的菜品信息，需要操作两张表
    public void saveWithDish(SetmealDto setmealDto);

    @Transactional
    //删除套餐，同时删除套餐关联表的信息，操作两张表
    public void removeWithDish(Long... ids);

    //根据id查询套餐信息和对应的菜品信息
    public SetmealDto getByIdWithDish(Long id);

    @Transactional
    //更新套餐信息和对应的菜品信息
    public void updateWithDish(SetmealDto setmealDto);


}
