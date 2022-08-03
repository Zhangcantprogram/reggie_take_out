package com.zhang.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhang.reggie.entity.Category;

public interface CategoryService extends IService<Category> {

    public void deleteByid(Long id);
}
