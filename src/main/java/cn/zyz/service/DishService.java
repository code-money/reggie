package cn.zyz.service;

import cn.zyz.dto.DishDto;
import cn.zyz.entity.Dish;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

public interface DishService extends IService<Dish> {

    @Transactional
    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表
    public void saveWithFlavor(DishDto dishDto);

    @Transactional
    //删除菜品，同时删除菜品关联表的信息，操作两张表
    public void removeWithFlavor(Long... ids);

    //根据id查询对应的菜品信息和口味信息
    public DishDto getByIdWithFlavor(Long id);

    @Transactional
    //更新菜品信息和对应的口味信息
    public void updateWithFlavor(DishDto dishDto);
}
