package dairyhub_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dairyhub_backend.entity.OrderItem;

public interface OrderItemRepository
        extends JpaRepository<OrderItem, Long> {

}