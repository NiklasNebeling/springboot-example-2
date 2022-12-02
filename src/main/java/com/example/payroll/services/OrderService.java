package com.example.payroll.services;

import com.example.payroll.controller.OrderModelAssembler;
import com.example.payroll.entities.Order;
import com.example.payroll.entities.Status;
import com.example.payroll.errorhandling.OrderNotFoundException;
import com.example.payroll.repositories.OrderRepository;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderModelAssembler assembler;

    public OrderService(OrderRepository orderRepository, OrderModelAssembler assembler) {
        this.orderRepository = orderRepository;
        this.assembler = assembler;
    }

    public List<EntityModel<Order>> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
    }

    public EntityModel<Order> getOrder(long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return assembler.toModel(order);
    }

    public Order addOrder(Order newOrder) {
        newOrder.setStatus(Status.IN_PROGRESS);
        return orderRepository.save(newOrder);
    }

    public Order completeOrder(long id) {
        Order order = orderRepository.findById(id) //
                .orElseThrow(() -> new OrderNotFoundException(id));
        if (order.getStatus() == Status.IN_PROGRESS) {
            order.setStatus(Status.COMPLETED);
        }
        return order;

    }

    public Order cancelOrder(long id) {
        Order order = orderRepository.findById(id) //
                .orElseThrow(() -> new OrderNotFoundException(id));
        if (order.getStatus() == Status.IN_PROGRESS) {
            order.setStatus(Status.CANCELLED);
        }
        return order;

    }

    public EntityModel<Order> assembleOrder(Order order) {
        return assembler.toModel(order);
    }

}
