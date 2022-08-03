package com.zhang.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhang.reggie.dto.DishDto;
import com.zhang.reggie.entity.Dish;
import com.zhang.reggie.entity.DishFlavor;
import com.zhang.reggie.mapper.DishMapper;
import com.zhang.reggie.service.DishFlavorService;
import com.zhang.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    public void saveWithFlavor(DishDto dishDto) {

        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);

        //由于dishDto中的flavor集合中没有设置dishId，因此需要获取之后再对flavor集合进行设置
        Long dishId = dishDto.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //保存菜品口味到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品的基础信息和口味的信息
     * @param id
     * @return
     */
    public DishDto getByIdWithFlavor(Long id) {
        DishDto dishDto = new DishDto();
        //查询菜品的基础信息
        Dish dish = this.getById(id);
        //对象拷贝
        BeanUtils.copyProperties(dish,dishDto);
        //查询对应口味的信息
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavorList = dishFlavorService.list(lqw);
        //设置口味
        dishDto.setFlavors(flavorList);
        return dishDto;
    }

    /**
     * 更新菜品信息，同时更新对应的口味信息
     * @param dishDto
     */
    @Transactional      //保持事务的一致性
    public void updateWithFlavor(DishDto dishDto) {
        //更新菜品的基础信息-----即对dish表的update操作
        this.updateById(dishDto);

        //更新菜品对应的口味信息
        //1、 先清理菜品对应的口味数据-----即对dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(lqw);

        //2、 再添加菜品新添加的口味数据-----即对dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();

        //设置dish_flavor表中的dishId字段
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }
}
