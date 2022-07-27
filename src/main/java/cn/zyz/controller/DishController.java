package cn.zyz.controller;

/**
 * @program: reggie
 * @author: zyz
 * @create: 2022-07-21 17:03
 **/

import cn.zyz.common.R;
import cn.zyz.dto.DishDto;
import cn.zyz.entity.Category;
import cn.zyz.entity.Dish;
import cn.zyz.entity.DishFlavor;
import cn.zyz.entity.Setmeal;
import cn.zyz.service.CategoryService;
import cn.zyz.service.DishFlavorService;
import cn.zyz.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 新增菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    @CacheEvict(value = "dishCache",allEntries = true)   //在新增数据时，清空redis中的dish缓存
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info("菜品信息:{}", dishDto.toString());
        dishService.saveWithFlavor(dishDto);

        /*//第一种：清理所有的菜品缓存数据
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);

        //第二种：清理某个分类下面的菜品缓存数据
        String key = "dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);*/
        
        return R.success("新增菜品成功");
    }


    /**
     * 菜品信息分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> pageQuery(Integer page, Integer pageSize, String name) {
        //构造分页查询
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> pageInfoDto = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null, Dish::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        dishService.page(pageInfo, queryWrapper);

        //对象拷贝
        //将分页查询后的pageInfo对象拷贝给pageInfoDto对象，除去records属性
        BeanUtils.copyProperties(pageInfo, pageInfoDto, "records");

        List<Dish> records = pageInfo.getRecords();
        //通过stream流方式得到DishDto集合
        List<DishDto> collect = records.stream().map(dish -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(dish, dishDto);

            Long categoryId = dish.getCategoryId();//分类Id
            //根据id查询分类对象的分类名称
            String categoryName = categoryService.getById(categoryId).getName();
            dishDto.setCategoryName(categoryName);
            return dishDto;
        }).collect(Collectors.toList());

        pageInfoDto.setRecords(collect);
        return R.success(pageInfoDto);
    }


    /**
     * 根据id查询对应的菜品信息和口味信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 更新菜品信息和对应的口味
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    @CacheEvict(value = "dishCache",allEntries = true)
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);

        /*//第一种：清理所有的菜品缓存数据
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);

        //第二种：清理某个分类下面的菜品缓存数据
        String key = "dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);*/

        return R.success("更新菜品信息成功");
    }


    /**
     * 启售和批量启售菜品
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status, Long... ids) {
        for (Long id : ids) {
            Dish dish = dishService.getById(id);
            dish.setStatus(status);
            dishService.updateById(dish);
        }
        return R.success("状态修改成功");
    }

    /**
     * 删除|批量删除菜品和对应的口味信息
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(value = "dishCache",allEntries = true)
    public R<String> delete(Long... ids){
        dishService.removeWithFlavor(ids);
        return R.success("删除成功");
    }


    /**
     * 根据条件查询对应菜品数据
     * @param categoryId
     * @param name
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value = "dishCache",key = "'dish_'+#categoryId+'_1'")
    public R<List> getByCategoryId(Long categoryId,String name,String status){
        /*//动态构造key，以菜品id加状态为key
        String key = "dish_"+categoryId+"_"+status;
        //先从redis中获取缓存数据
        List<DishDto> redisList = (List<DishDto>) redisTemplate.opsForValue().get(key);

        if (redisList!=null){
            //如果存在，直接返回，无需查询数据库
            log.info("从redis缓存中拿到数据");
            return R.success(redisList);
        }*/


        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();

        //添加条件，查询状态为1的（起售状态）
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.eq(categoryId!=null,Dish::getCategoryId,categoryId);
        queryWrapper.like(name!=null,Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dishDtos = list.stream().map(dish -> {
            //根据Dish查询到对应的口味信息集合
            //SQL: select * from dish_flavor where dish_id = ?
            LambdaQueryWrapper<DishFlavor> flavorWrapper = new LambdaQueryWrapper<>();
            flavorWrapper.eq(DishFlavor::getDishId, dish.getId());
            List<DishFlavor> flavors = dishFlavorService.list(flavorWrapper);

            //将口味信息封装到拷贝的DishDto中
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish, dishDto);
            dishDto.setFlavors(flavors);
            return dishDto;

        }).collect(Collectors.toList());

        //如果redis缓存中不存在，将查询到的菜品数据缓存到Redis
//        redisTemplate.opsForValue().set(key,dishDtos,60, TimeUnit.MINUTES);

        return R.success(dishDtos);
    }
}



