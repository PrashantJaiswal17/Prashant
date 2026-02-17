package com.example.ordermanagement.events;

import com.example.ordermanagement.domain.OrderStatus;
import java.time.Instant;
import java.util.UUID;

public record OrderProcessedEvent(
        UUID orderId,
        String requestId,
        OrderStatus status,
        String reason,
        Instant processedAt
) {
}
