package com.zhang.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 * 利用ControllerAdvice来进行全局的异常处理，对使用了RestController和Controller
 * 的注解进行管理，当出现对应的异常时，执行相应的方法
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 异常处理方法
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());
        //当出现相同的员工账号时，数据库回抛出SQLIntegrityConstraintViolationException异常
        if (ex.getMessage().contains("Duplicate entry")){
            //如果异常信息包涵 "Duplicate entry"，则说明员工账号已存在
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已存在!";
            return R.error(msg);
        }
        return R.error("未知错误！");
    }

    /**
     * 异常处理方法
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex){
        log.error(ex.getMessage());

        return R.error(ex.getMessage());
    }
}
