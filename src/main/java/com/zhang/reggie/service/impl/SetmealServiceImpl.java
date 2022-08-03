package com.zhang.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhang.reggie.common.CustomException;
import com.zhang.reggie.dto.SetmealDto;
import com.zhang.reggie.entity.Setmeal;
import com.zhang.reggie.entity.SetmealDish;
import com.zhang.reggie.mapper.SetmealMapper;
import com.zhang.reggie.service.SetmealDishService;
import com.zhang.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，同时保存套餐和菜品的关联关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息，操作setmeal表，执行insert操作
        this.save(setmealDto);

        //保存套餐和菜品的关联关系，操作setmeal_dish表，执行insert操作
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //设置setmealDishes集合中的setmealId
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐，同时删除套餐和菜品的关联关系
     * @param ids
     */
    public void removeWithDish(List<Long> ids) {
        //查询套餐是否为启售状态，若为启售状态，则不能删除
        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
        setmealQueryWrapper.in(Setmeal::getId,ids);
        setmealQueryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(setmealQueryWrapper);

        if (count > 0){
            //如果不能删除，则抛出一个业务异常
            throw new CustomException("套餐正在售卖，不能删除！");
        }

        //若不为启售状态，则先删除套餐表setmeal中的数据
        this.removeByIds(ids);

        //再删除关系表setmeal_dish中的数据
        LambdaQueryWrapper<SetmealDish> setmealDishQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishQueryWrapper.in(SetmealDish::getSetmealId,ids);

        setmealDishService.remove(setmealDishQueryWrapper);
    }
}
