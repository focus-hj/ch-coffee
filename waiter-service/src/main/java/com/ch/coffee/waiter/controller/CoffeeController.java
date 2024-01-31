package com.ch.coffee.waiter.controller;

import com.ch.coffee.waiter.controller.request.NewCoffeeRequest;
import com.ch.coffee.waiter.model.Coffee;
import com.ch.coffee.waiter.service.CoffeeService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/coffee")
@RateLimiter(name = "coffee")
@Slf4j
public class CoffeeController {
    @Autowired
    private CoffeeService coffeeService;

    @PostMapping(path = "/")
    @ResponseStatus(HttpStatus.CREATED)
    public Coffee addCoffee(@Valid @RequestBody NewCoffeeRequest request) {
        return coffeeService.saveCoffee(request.getName(), request.getPrice());
    }

    @GetMapping(path = "/", params = "!name")
    public List<Coffee> getAll() {
        return coffeeService.getAllCoffee();
    }

    @GetMapping("/{id}")
    public Coffee getById(@PathVariable Long id) {
        Coffee coffee = coffeeService.getCoffee(id);
        log.info("Coffee {}:", coffee);
        return coffee;
    }

    @GetMapping(path = "/", params = "name")
    public Coffee getByName(@RequestParam String name) {
        return coffeeService.getCoffee(name);
    }
}
