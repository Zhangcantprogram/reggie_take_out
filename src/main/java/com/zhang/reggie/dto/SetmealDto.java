package com.zhang.reggie.dto;

import com.zhang.reggie.entity.Setmeal;
import com.zhang.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
