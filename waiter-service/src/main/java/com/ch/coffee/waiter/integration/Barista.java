package com.ch.coffee.waiter.integration;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface Barista {
    String NEW_ORDERS = "newOrders";
    String FINISHED_ORDER = "finishedOrders";

    @Input
    SubscribableChannel finishedOrders();

    @Output
    MessageChannel newOrders();
}