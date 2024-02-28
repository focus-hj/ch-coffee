package com.ch.coffee.barista.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum OrderStateEnum {
    INIT(0, "创建"),
    PAID(1, "支付"),
    BREWING(2, "制作中"),
    BREWED(3, "制作完成"),
    TAKEN(4, "已取单"),
    CANCELLED(5, "已取消");

    private Integer code;
    private String desc;
}
