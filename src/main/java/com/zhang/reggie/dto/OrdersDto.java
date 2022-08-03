package com.zhang.reggie.dto;

import com.zhang.reggie.entity.OrderDetail;
import com.zhang.reggie.entity.Orders;
import lombok.Data;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;

    private int sumNum;
}
