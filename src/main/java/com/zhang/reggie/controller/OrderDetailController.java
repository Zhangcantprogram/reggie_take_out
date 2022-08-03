package com.zhang.reggie.controller;

import com.zhang.reggie.service.OrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@Slf4j
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;
}
