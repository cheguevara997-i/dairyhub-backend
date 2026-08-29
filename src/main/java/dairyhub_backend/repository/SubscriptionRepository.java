package dairyhub_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dairyhub_backend.entity.Subscription;

public interface SubscriptionRepository
        extends JpaRepository<Subscription, Long> {

    List<Subscription> findByCustomerEmail(String customerEmail);

}