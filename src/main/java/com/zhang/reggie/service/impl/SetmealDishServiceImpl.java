package com.zhang.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhang.reggie.entity.SetmealDish;
import com.zhang.reggie.mapper.SetmealDishMapper;
import com.zhang.reggie.mapper.SetmealMapper;
import com.zhang.reggie.service.SetmealDishService;
import com.zhang.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
}
