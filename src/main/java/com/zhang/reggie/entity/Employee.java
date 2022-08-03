package com.zhang.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;//身份证号

    private Integer status;

    @TableField(fill = FieldFill.INSERT)//插入时，公共字段自动更新
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)//插入和更新时，公共字段自动更新
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)//插入时，公共字段自动更新
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)//插入和更新时，公共字段自动更新
    private Long updateUser;

}
