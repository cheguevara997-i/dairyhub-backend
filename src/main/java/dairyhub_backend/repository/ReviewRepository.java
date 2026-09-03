package dairyhub_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dairyhub_backend.entity.Review;

public interface ReviewRepository
        extends JpaRepository<Review, Long> {

    // All reviews for a product
    List<Review> findByProductIdOrderByCreatedAtDesc(
            Long productId
    );


    // Check whether customer already reviewed product
    Optional<Review> findByProductIdAndUserEmail(
            Long productId,
            String userEmail
    );


    // Count reviews for product
    long countByProductId(
            Long productId
    );


    // Admin - latest reviews first
    List<Review> findAllByOrderByCreatedAtDesc();
}