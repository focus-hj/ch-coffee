package com.ch.coffee.waiter.controller;

import com.ch.coffee.waiter.controller.request.NewOrderRequest;
import com.ch.coffee.waiter.controller.request.OrderStateRequest;
import com.ch.coffee.waiter.model.Coffee;
import com.ch.coffee.waiter.model.CoffeeOrder;
import com.ch.coffee.waiter.service.CoffeeOrderService;
import com.ch.coffee.waiter.service.CoffeeService;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@Slf4j
public class CoffeeOrderController {
    @Autowired
    private CoffeeOrderService orderService;
    @Autowired
    private CoffeeService coffeeService;
    private RateLimiter rateLimiter;

    public CoffeeOrderController(RateLimiterRegistry rateLimiterRegistry) {
        rateLimiter = rateLimiterRegistry.rateLimiter("order");
    }

    @PostMapping(path = "/")
    @ResponseStatus(HttpStatus.CREATED)
    @io.github.resilience4j.ratelimiter.annotation.RateLimiter(name = "order")
    public CoffeeOrder create(@RequestBody NewOrderRequest request) {
        log.info("Receive new Order {}", request);
        Coffee[] coffeeList = coffeeService.getCoffeeByName(request.getItems()).toArray(new Coffee[]{});
        return orderService.createOrder(request.getCustomer(), coffeeList);
    }

    @PutMapping("/{id}")
    public CoffeeOrder updateState(@PathVariable("id") Long id,
                                   @RequestBody OrderStateRequest request) {
        log.info("Update order {} witch state {}", id, request);
        CoffeeOrder order = orderService.get(id);
        orderService.updateState(order, request.getState());
        return order;
    }

    @GetMapping("/{id}")
    public CoffeeOrder getOrder(@PathVariable("id") Long id) {
        CoffeeOrder order = null;
        try {
            order = rateLimiter.executeSupplier(() -> orderService.get(id));
            log.info("Get Order: {}", order);
        } catch (RequestNotPermitted e) {
            log.warn("Request Not Permitted! {}", e.getMessage());
        }
        return order;
    }
}
