package com.ch.coffee.customer.model;

import lombok.*;
import org.joda.money.Money;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoffeeOrder {
    private Long id;
    private String customer;
    private List<Coffee> items;
    private Integer state;
    private Integer discount;
    private Money total;
    private String waiter;
    private String barista;
    private Date createTime;
    private Date updateTime;
}
