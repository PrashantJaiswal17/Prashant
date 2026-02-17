package com.example.ordermanagement.repository;

import com.example.ordermanagement.domain.OrderEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
    Optional<OrderEntity> findByRequestId(String requestId);
}
