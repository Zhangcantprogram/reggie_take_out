package com.zhang.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhang.reggie.dto.DishDto;
import com.zhang.reggie.entity.Dish;

public interface DishService extends IService<Dish> {
    //新增菜品，保存菜品和选择的口味数据，同时操作两张表，分别为dish，dish_flavor
    public void saveWithFlavor(DishDto dishDto);
    //根据id查询菜品的基础信息和口味的信息
    public DishDto getByIdWithFlavor(Long id);
    //更新菜品信息，同时更新对应的口味信息
    public void updateWithFlavor(DishDto dishDto);
}
