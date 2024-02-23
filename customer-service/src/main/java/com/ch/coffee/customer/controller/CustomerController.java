package com.ch.coffee.customer.controller;

import com.ch.coffee.customer.controller.request.NewOrderRequest;
import com.ch.coffee.customer.controller.request.OrderStateRequest;
import com.ch.coffee.customer.integration.CoffeeOrderService;
import com.ch.coffee.customer.integration.CoffeeService;
import com.ch.coffee.customer.model.Coffee;
import com.ch.coffee.customer.model.CoffeeOrder;
import com.ch.coffee.customer.model.OrderStateEnum;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerOpenException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/customer")
@Slf4j
public class CustomerController {

    @Autowired
    private CoffeeService coffeeService;
    @Autowired
    private CoffeeOrderService orderService;
    @Value("${customer.name}")
    private String customer;
    private CircuitBreaker circuitBreaker;
    private Bulkhead bulkhead;

    public CustomerController(CircuitBreakerRegistry circuitBreakerRegistry,
                              BulkheadRegistry bulkheadRegistry) {
        circuitBreaker = circuitBreakerRegistry.circuitBreaker("menu");
        bulkhead = bulkheadRegistry.bulkhead("menu");
    }

    @GetMapping("/menu")
    public List<Coffee> readMenu() {
        return Try.ofSupplier(
                        Bulkhead.decorateSupplier(bulkhead,
                                CircuitBreaker.decorateSupplier(circuitBreaker,
                                        () -> coffeeService.getAll())))
                .recover(CircuitBreakerOpenException.class, Collections.emptyList())
                .recover(BulkheadFullException.class, Collections.emptyList())
                .get();
    }

    @PostMapping("/order")
    @io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(name = "order")
    @io.github.resilience4j.bulkhead.annotation.Bulkhead(name = "order")
    public CoffeeOrder createAndPayOrder() {
        NewOrderRequest orderRequest = NewOrderRequest.builder()
                .customer(customer)
                .items(Arrays.asList("capuccino"))
                .build();
        CoffeeOrder order = orderService.create(orderRequest);
        log.info("Create order: {}", order != null ? order.getId() : "-");
        order = orderService.updateState(order.getId(),
                OrderStateRequest.builder().state(OrderStateEnum.PAID.getCode()).build());
        log.info("Order is PAID: {}", order);
        return order;
    }
}
