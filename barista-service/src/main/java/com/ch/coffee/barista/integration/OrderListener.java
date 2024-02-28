package com.ch.coffee.barista.integration;

import com.ch.coffee.barista.model.CoffeeOrder;
import com.ch.coffee.barista.model.OrderStateEnum;
import com.ch.coffee.barista.repository.CoffeeOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@Transactional
public class OrderListener {
    @Autowired
    private CoffeeOrderRepository orderRepository;
    @Autowired
    @Qualifier(Waiter.FINISHED_ORDERS)
    private MessageChannel finishedOrdersMessageChannel;
    @Value("${order.barista-prefix}${random.uuid}")
    private String barista;

    @StreamListener(Waiter.NEW_ORDERS)
    public void processNewOrder(Long id) {
        CoffeeOrder order = orderRepository.getOne(id);
        if (order == null) {
            log.warn("Order id {} is NOT valid.", id);
            return;
        }
        log.info("Receive a new Order {}. Waiter: {}. Customer: {}.",
                id, order.getWaiter(), order.getCustomer());
        order.setState(OrderStateEnum.BREWED.getCode());
        order.setBarista(barista);
        orderRepository.save(order);
        log.info("Order {} is READY.", id);
        finishedOrdersMessageChannel.send(MessageBuilder.withPayload(id).build());
    }
}
