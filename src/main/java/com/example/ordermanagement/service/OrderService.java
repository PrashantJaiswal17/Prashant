package com.example.ordermanagement.service;

import com.example.ordermanagement.domain.OrderEntity;
import com.example.ordermanagement.domain.OrderStatus;
import com.example.ordermanagement.dto.CreateOrderRequest;
import com.example.ordermanagement.dto.OrderResponse;
import com.example.ordermanagement.events.OrderCreatedEvent;
import com.example.ordermanagement.events.OrderProcessedEvent;
import com.example.ordermanagement.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;

    public OrderService(OrderRepository orderRepository, OrderEventPublisher orderEventPublisher) {
        this.orderRepository = orderRepository;
        this.orderEventPublisher = orderEventPublisher;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        return orderRepository.findByRequestId(request.requestId())
                .map(this::toResponse)
                .orElseGet(() -> createAndPublish(request));
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(UUID orderId) {
        return toResponse(orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId)));
    }

    @Transactional
    public void processOrderCreated(OrderCreatedEvent event) {
        OrderEntity order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + event.orderId()));

        OrderStatus nextStatus = event.amount().signum() > 0 ? OrderStatus.COMPLETED : OrderStatus.FAILED;
        order.setStatus(nextStatus);
        order.setFailureReason(nextStatus == OrderStatus.FAILED ? "Amount must be greater than 0" : null);
        order.setUpdatedAt(Instant.now());

        orderRepository.save(order);

        orderEventPublisher.publishProcessed(new OrderProcessedEvent(
                order.getId(),
                order.getRequestId(),
                order.getStatus(),
                order.getFailureReason(),
                order.getUpdatedAt()
        ));
    }

    private OrderResponse createAndPublish(CreateOrderRequest request) {
        Instant now = Instant.now();
        OrderEntity entity = new OrderEntity();
        entity.setId(UUID.randomUUID());
        entity.setRequestId(request.requestId());
        entity.setCustomerId(request.customerId());
        entity.setAmount(request.amount());
        entity.setStatus(OrderStatus.CREATED);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        OrderEntity saved = orderRepository.save(entity);
        orderEventPublisher.publishCreated(new OrderCreatedEvent(
                saved.getId(),
                saved.getRequestId(),
                saved.getCustomerId(),
                saved.getAmount(),
                saved.getCreatedAt()
        ));

        return toResponse(saved);
    }

    private OrderResponse toResponse(OrderEntity entity) {
        return new OrderResponse(
                entity.getId(),
                entity.getRequestId(),
                entity.getCustomerId(),
                entity.getAmount(),
                entity.getStatus(),
                entity.getFailureReason(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
