package com.ch.coffee.customer.integration;

import com.ch.coffee.customer.controller.request.OrderStateRequest;
import com.ch.coffee.customer.model.CoffeeOrder;
import com.ch.coffee.customer.model.OrderStateEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
public class NotificationListener {

    @Autowired
    private CoffeeOrderService orderService;

    @Value("${customer.name}")
    private String customer;

    @StreamListener(Waiter.NOTIFY_ORDERS)
    public void takeOrder(@Payload Long id) {
        CoffeeOrder order = orderService.getOrder(id);
        if (Objects.equals(order.getState(), OrderStateEnum.BREWED.getCode())) {
            log.info("Order {} is READY, I'll take it.", id);
            orderService.updateState(id, OrderStateRequest.builder().state(OrderStateEnum.TAKEN.getCode()).build());
        } else {
            log.warn("Order {} is NOT READY. Why are you notify me?", id);
        }
    }
}
