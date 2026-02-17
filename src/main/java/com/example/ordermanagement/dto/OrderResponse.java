package com.example.ordermanagement.dto;

import com.example.ordermanagement.domain.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String requestId,
        String customerId,
        BigDecimal amount,
        OrderStatus status,
        String failureReason,
        Instant createdAt,
        Instant updatedAt
) {
}
