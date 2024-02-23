package com.ch.coffee.customer.controller.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
public class NewOrderRequest {

    private String customer;
    private List<String> items;
}
