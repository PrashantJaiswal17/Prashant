package com.example.ordermanagement.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID orderId,
        String requestId,
        String customerId,
        BigDecimal amount,
        Instant createdAt
) {
}
