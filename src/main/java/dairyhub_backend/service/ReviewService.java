package dairyhub_backend.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import dairyhub_backend.entity.CustomerOrder;
import dairyhub_backend.entity.OrderItem;
import dairyhub_backend.entity.Review;
import dairyhub_backend.repository.OrderRepository;
import dairyhub_backend.repository.ReviewRepository;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public ReviewService(
            ReviewRepository reviewRepository,
            OrderRepository orderRepository) {

        this.reviewRepository = reviewRepository;
        this.orderRepository = orderRepository;
    }


    // =====================================================
    // ADD REVIEW
    // =====================================================

    public Review addReview(
            Long productId,
            String productName,
            String userEmail,
            String userName,
            Integer rating,
            String comment) {

        // ---------------------------------------------
        // BASIC VALIDATION
        // ---------------------------------------------

        if (productId == null) {

            throw new RuntimeException(
                    "Product ID is required."
            );
        }


        if (userEmail == null
                || userEmail.trim().isEmpty()) {

            throw new RuntimeException(
                    "User email is required."
            );
        }


        if (userName == null
                || userName.trim().isEmpty()) {

            throw new RuntimeException(
                    "User name is required."
            );
        }


        if (rating == null
                || rating < 1
                || rating > 5) {

            throw new RuntimeException(
                    "Rating must be between 1 and 5."
            );
        }


        if (comment == null
                || comment.trim().isEmpty()) {

            throw new RuntimeException(
                    "Review comment is required."
            );
        }


        // ---------------------------------------------
        // CHECK DUPLICATE REVIEW
        // ---------------------------------------------

        Optional<Review> existingReview =
                reviewRepository
                        .findByProductIdAndUserEmail(
                                productId,
                                userEmail.trim()
                        );


        if (existingReview.isPresent()) {

            throw new RuntimeException(
                    "You have already reviewed this product."
            );
        }


        // ---------------------------------------------
        // CHECK PURCHASE / DELIVERY STATUS
        // ---------------------------------------------

        boolean purchased = false;

        boolean delivered = false;


        List<CustomerOrder> customerOrders =
                orderRepository
                        .findByCustomerEmail(
                                userEmail.trim()
                        );


        for (CustomerOrder order :
                customerOrders) {

            if (order.getItems() == null) {
                continue;
            }


            for (OrderItem item :
                    order.getItems()) {

                if (item.getProductId() != null
                        && item.getProductId()
                                .equals(productId)) {

                    purchased = true;


                    if ("DELIVERED".equalsIgnoreCase(
                            order.getStatus())) {

                        delivered = true;
                    }


                    break;
                }
            }


            if (delivered) {
                break;
            }
        }


        // ---------------------------------------------
        // CREATE REVIEW
        // ---------------------------------------------

        Review review = new Review();


        review.setProductId(
                productId
        );


        review.setProductName(
                productName
        );


        review.setUserEmail(
                userEmail.trim()
        );


        review.setUserName(
                userName.trim()
        );


        review.setRating(
                rating
        );


        review.setComment(
                comment.trim()
        );


        // ---------------------------------------------
        // VERIFIED PURCHASE
        // ---------------------------------------------
        /*
         * Customer can review even without purchase.
         *
         * Delivered purchase:
         * verifiedPurchase = true
         *
         * Otherwise:
         * verifiedPurchase = false
         */

        review.setVerifiedPurchase(
                purchased && delivered
        );


        review.setCreatedAt(
                LocalDateTime.now()
        );


        // ---------------------------------------------
        // SAVE REVIEW
        // ---------------------------------------------

        return reviewRepository.save(
                review
        );
    }


    // =====================================================
    // GET PRODUCT REVIEWS
    // =====================================================

    public List<Review> getProductReviews(
            Long productId) {

        return reviewRepository
                .findByProductIdOrderByCreatedAtDesc(
                        productId
                );
    }


    // =====================================================
    // GET AVERAGE RATING
    // =====================================================

    public Double getAverageRating(
            Long productId) {

        List<Review> reviews =
                reviewRepository
                        .findByProductIdOrderByCreatedAtDesc(
                                productId
                        );


        if (reviews.isEmpty()) {

            return 0.0;
        }


        double total = 0;

        int validRatings = 0;


        for (Review review :
                reviews) {

            if (review.getRating() != null) {

                total += review.getRating();

                validRatings++;
            }
        }


        if (validRatings == 0) {

            return 0.0;
        }


        double average =
                total / validRatings;


        return Math.round(
                average * 10.0
        ) / 10.0;
    }


    // =====================================================
    // GET REVIEW COUNT
    // =====================================================

    public long getReviewCount(
            Long productId) {

        return reviewRepository
                .countByProductId(
                        productId
                );
    }


    // =====================================================
    // CHECK REVIEW ELIGIBILITY
    // =====================================================

    public Map<String, Object> getReviewEligibility(
            Long productId,
            String userEmail) {

        Map<String, Object> result =
                new HashMap<>();


        // ---------------------------------------------
        // LOGIN REQUIRED
        // ---------------------------------------------

        if (userEmail == null
                || userEmail.trim().isEmpty()) {

            result.put(
                    "canReview",
                    false
            );


            result.put(
                    "reason",
                    "LOGIN_REQUIRED"
            );


            result.put(
                    "purchased",
                    false
            );


            result.put(
                    "delivered",
                    false
            );


            return result;
        }


        String normalizedEmail =
                userEmail.trim();


        // ---------------------------------------------
        // CHECK EXISTING REVIEW
        // ---------------------------------------------

        Optional<Review> existingReview =
                reviewRepository
                        .findByProductIdAndUserEmail(
                                productId,
                                normalizedEmail
                        );


        if (existingReview.isPresent()) {

            Review review =
                    existingReview.get();


            result.put(
                    "canReview",
                    false
            );


            result.put(
                    "reason",
                    "ALREADY_REVIEWED"
            );


            result.put(
                    "purchased",
                    false
            );


            result.put(
                    "delivered",
                    false
            );


            result.put(
                    "existingReviewId",
                    review.getId()
            );


            return result;
        }


        // ---------------------------------------------
        // CHECK ORDERS
        // ---------------------------------------------

        boolean purchased = false;

        boolean delivered = false;


        List<CustomerOrder> customerOrders =
                orderRepository
                        .findByCustomerEmail(
                                normalizedEmail
                        );


        for (CustomerOrder order :
                customerOrders) {

            if (order.getItems() == null) {
                continue;
            }


            for (OrderItem item :
                    order.getItems()) {

                if (item.getProductId() != null
                        && item.getProductId()
                                .equals(productId)) {

                    purchased = true;


                    if ("DELIVERED".equalsIgnoreCase(
                            order.getStatus())) {

                        delivered = true;
                    }


                    break;
                }
            }


            if (delivered) {
                break;
            }
        }


        // ---------------------------------------------
        // CUSTOMER CAN REVIEW
        // ---------------------------------------------
        /*
         * Logged-in customers can review
         * even when they have not purchased.
         *
         * Delivered customers get
         * verifiedPurchase = true.
         */

        result.put(
                "canReview",
                true
        );


        result.put(
                "purchased",
                purchased
        );


        result.put(
                "delivered",
                delivered
        );


        if (delivered) {

            result.put(
                    "reason",
                    "VERIFIED_ELIGIBLE"
            );

        } else if (purchased) {

            result.put(
                    "reason",
                    "PURCHASED_NOT_DELIVERED"
            );

        } else {

            result.put(
                    "reason",
                    "GENERAL_REVIEW"
            );
        }


        return result;
    }


    // =====================================================
    // UPDATE CUSTOMER REVIEW
    // =====================================================

    public Review updateReview(
            Long reviewId,
            String userEmail,
            Integer rating,
            String comment) {

        // ---------------------------------------------
        // USER EMAIL CHECK
        // ---------------------------------------------

        if (userEmail == null
                || userEmail.trim().isEmpty()) {

            throw new RuntimeException(
                    "User email is required."
            );
        }


        // ---------------------------------------------
        // RATING CHECK
        // ---------------------------------------------

        if (rating == null
                || rating < 1
                || rating > 5) {

            throw new RuntimeException(
                    "Rating must be between 1 and 5."
            );
        }


        // ---------------------------------------------
        // COMMENT CHECK
        // ---------------------------------------------

        if (comment == null
                || comment.trim().isEmpty()) {

            throw new RuntimeException(
                    "Review comment is required."
            );
        }


        // ---------------------------------------------
        // FIND REVIEW
        // ---------------------------------------------

        Optional<Review> optionalReview =
                reviewRepository.findById(
                        reviewId
                );


        if (optionalReview.isEmpty()) {

            throw new RuntimeException(
                    "Review not found."
            );
        }


        Review review =
                optionalReview.get();


        // ---------------------------------------------
        // OWNERSHIP CHECK
        // ---------------------------------------------

        if (review.getUserEmail() == null
                || !review.getUserEmail()
                        .equalsIgnoreCase(
                                userEmail.trim()
                        )) {

            throw new RuntimeException(
                    "You can edit only your own review."
            );
        }


        // ---------------------------------------------
        // UPDATE REVIEW
        // ---------------------------------------------

        review.setRating(
                rating
        );


        review.setComment(
                comment.trim()
        );


        // ---------------------------------------------
        // SAVE
        // ---------------------------------------------

        return reviewRepository.save(
                review
        );
    }


    // =====================================================
    // DELETE CUSTOMER'S OWN REVIEW
    // =====================================================

    public boolean deleteOwnReview(
            Long reviewId,
            String userEmail) {

        // ---------------------------------------------
        // USER EMAIL CHECK
        // ---------------------------------------------

        if (userEmail == null
                || userEmail.trim().isEmpty()) {

            throw new RuntimeException(
                    "User email is required."
            );
        }


        // ---------------------------------------------
        // FIND REVIEW
        // ---------------------------------------------

        Optional<Review> optionalReview =
                reviewRepository.findById(
                        reviewId
                );


        if (optionalReview.isEmpty()) {

            return false;
        }


        Review review =
                optionalReview.get();


        // ---------------------------------------------
        // OWNERSHIP CHECK
        // ---------------------------------------------

        if (review.getUserEmail() == null
                || !review.getUserEmail()
                        .equalsIgnoreCase(
                                userEmail.trim()
                        )) {

            throw new RuntimeException(
                    "You can delete only your own review."
            );
        }


        // ---------------------------------------------
        // DELETE REVIEW
        // ---------------------------------------------

        reviewRepository.deleteById(
                reviewId
        );


        return true;
    }


    // =====================================================
    // GET ALL REVIEWS
    // ADMIN
    // =====================================================

    public List<Review> getAllReviews() {

        return reviewRepository
                .findAllByOrderByCreatedAtDesc();
    }


    // =====================================================
    // DELETE REVIEW
    // ADMIN
    // =====================================================

    public boolean deleteReview(
            Long id) {

        if (!reviewRepository.existsById(
                id)) {

            return false;
        }


        reviewRepository.deleteById(
                id
        );


        return true;
    }
}