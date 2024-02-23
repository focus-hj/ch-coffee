package com.ch.coffee.customer.support;

import com.ch.coffee.customer.model.CoffeeOrder;
import org.springframework.context.ApplicationEvent;

public class OrderWaitingEvent extends ApplicationEvent {

    private CoffeeOrder order;

    public OrderWaitingEvent(CoffeeOrder order) {
        super(order);
        this.order = order;
    }
}
