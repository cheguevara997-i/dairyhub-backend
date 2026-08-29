package dairyhub_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dairyhub_backend.entity.CustomerOrder;

public interface OrderRepository
        extends JpaRepository<CustomerOrder, Long> {

    List<CustomerOrder> findByCustomerEmail(String customerEmail);
}