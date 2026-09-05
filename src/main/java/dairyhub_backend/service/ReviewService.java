package dairyhub_backend.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import dairyhub_backend.entity.CustomerOrder;
import dairyhub_backend.entity.OrderItem;
import dairyhub_backend.entity.Product;
import dairyhub_backend.entity.Review;
import dairyhub_backend.repository.OrderRepository;
import dairyhub_backend.repository.ProductRepository;
import dairyhub_backend.repository.ReviewRepository;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public ReviewService(
            ReviewRepository reviewRepository,
            OrderRepository orderRepository,
            ProductRepository productRepository) {

        this.reviewRepository =
                reviewRepository;

        this.orderRepository =
                orderRepository;

        this.productRepository =
                productRepository;
    }


    // =====================================================
    // NORMALIZE EMAIL
    // =====================================================

    private String normalizeEmail(
            String email) {

        if (
                email == null ||
                email.trim().isEmpty()
        ) {

            return null;
        }


        return email
                .trim()
                .toLowerCase();
    }


    // =====================================================
    // VALIDATE RATING
    // =====================================================

    private void validateRating(
            Integer rating) {

        if (
                rating == null ||
                rating < 1 ||
                rating > 5
        ) {

            throw new RuntimeException(
                    "Rating must be between 1 and 5."
            );
        }
    }


    // =====================================================
    // VALIDATE COMMENT
    // =====================================================

    private String validateComment(
            String comment) {

        if (
                comment == null ||
                comment.trim().isEmpty()
        ) {

            throw new RuntimeException(
                    "Review comment is required."
            );
        }


        String cleanedComment =
                comment.trim();


        if (
                cleanedComment.length() > 1000
        ) {

            throw new RuntimeException(
                    "Review comment cannot exceed 1000 characters."
            );
        }


        return cleanedComment;
    }


    // =====================================================
    // CHECK PURCHASE STATUS
    // =====================================================

    private Map<String, Boolean>
    checkPurchaseStatus(
            Long productId,
            String userEmail) {

        boolean purchased =
                false;

        boolean delivered =
                false;


        List<CustomerOrder> orders =
                orderRepository
                        .findByCustomerEmail(
                                userEmail
                        );


        for (
                CustomerOrder order :
                orders
        ) {

            if (
                    order.getItems() == null
            ) {

                continue;
            }


            for (
                    OrderItem item :
                    order.getItems()
            ) {

                if (
                        item.getProductId() != null
                        &&
                        item.getProductId()
                                .equals(productId)
                ) {

                    purchased =
                            true;


                    if (
                            "DELIVERED"
                                    .equalsIgnoreCase(
                                            order.getStatus()
                                    )
                    ) {

                        delivered =
                                true;
                    }


                    break;
                }

            }


            if (
                    delivered
            ) {

                break;
            }

        }


        Map<String, Boolean> result =
                new HashMap<>();


        result.put(
                "purchased",
                purchased
        );


        result.put(
                "delivered",
                delivered
        );


        return result;
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
        // PRODUCT ID
        // ---------------------------------------------

        if (
                productId == null
        ) {

            throw new RuntimeException(
                    "Product ID is required."
            );
        }


        // ---------------------------------------------
        // EMAIL
        // ---------------------------------------------

        String normalizedEmail =
                normalizeEmail(
                        userEmail
                );


        if (
                normalizedEmail == null
        ) {

            throw new RuntimeException(
                    "User email is required."
            );
        }


        // ---------------------------------------------
        // USER NAME
        // ---------------------------------------------

        if (
                userName == null ||
                userName.trim().isEmpty()
        ) {

            throw new RuntimeException(
                    "User name is required."
            );
        }


        // ---------------------------------------------
        // RATING
        // ---------------------------------------------

        validateRating(
                rating
        );


        // ---------------------------------------------
        // COMMENT
        // ---------------------------------------------

        String cleanedComment =
                validateComment(
                        comment
                );


        // ---------------------------------------------
        // DUPLICATE REVIEW CHECK
        // ---------------------------------------------

        Optional<Review> existingReview =
                reviewRepository
                        .findByProductIdAndUserEmail(
                                productId,
                                normalizedEmail
                        );


        if (
                existingReview.isPresent()
        ) {

            throw new RuntimeException(
                    "You have already reviewed this product."
            );
        }


        // ---------------------------------------------
        // PURCHASE / DELIVERY CHECK
        // ---------------------------------------------

        Map<String, Boolean> purchaseStatus =
                checkPurchaseStatus(
                        productId,
                        normalizedEmail
                );


        boolean purchased =
                Boolean.TRUE.equals(
                        purchaseStatus.get(
                                "purchased"
                        )
                );


        boolean delivered =
                Boolean.TRUE.equals(
                        purchaseStatus.get(
                                "delivered"
                        )
                );


        // ---------------------------------------------
        // MUST HAVE PURCHASED
        // ---------------------------------------------

        if (
                !purchased
        ) {

            throw new RuntimeException(
                    "You can review this product only after purchasing it."
            );
        }


        // ---------------------------------------------
        // MUST BE DELIVERED
        // ---------------------------------------------

        if (
                !delivered
        ) {

            throw new RuntimeException(
                    "You can review this product after your order has been delivered."
            );
        }


        // ---------------------------------------------
        // CREATE REVIEW
        // ---------------------------------------------

        Review review =
                new Review();


        review.setProductId(
                productId
        );


        review.setProductName(
                productName != null
                        ? productName.trim()
                        : ""
        );


        review.setUserEmail(
                normalizedEmail
        );


        review.setUserName(
                userName.trim()
        );


        review.setRating(
                rating
        );


        review.setComment(
                cleanedComment
        );


        // Delivered purchase = verified
        review.setVerifiedPurchase(
                true
        );


        review.setCreatedAt(
                LocalDateTime.now()
        );


        return reviewRepository.save(
                review
        );
    }


    // =====================================================
    // GET PRODUCT REVIEWS
    // =====================================================

    public List<Review> getProductReviews(
            Long productId) {

        if (
                productId == null
        ) {

            return List.of();
        }


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

        if (
                productId == null
        ) {

            return 0.0;
        }


        List<Review> reviews =
                reviewRepository
                        .findByProductIdOrderByCreatedAtDesc(
                                productId
                        );


        if (
                reviews.isEmpty()
        ) {

            return 0.0;
        }


        double total =
                0.0;

        int validRatings =
                0;


        for (
                Review review :
                reviews
        ) {

            Integer reviewRating =
                    review.getRating();


            if (
                    reviewRating != null &&
                    reviewRating >= 1 &&
                    reviewRating <= 5
            ) {

                total +=
                        reviewRating;

                validRatings++;
            }

        }


        if (
                validRatings == 0
        ) {

            return 0.0;
        }


        double average =
                total /
                validRatings;


        return Math.round(
                average * 10.0
        ) / 10.0;
    }


    // =====================================================
    // GET REVIEW COUNT
    // =====================================================

    public long getReviewCount(
            Long productId) {

        if (
                productId == null
        ) {

            return 0;
        }


        return reviewRepository
                .countByProductId(
                        productId
                );
    }


    // =====================================================
    // CHECK REVIEW ELIGIBILITY
    // =====================================================

    public Map<String, Object>
    getReviewEligibility(
            Long productId,
            String userEmail) {

        Map<String, Object> result =
                new HashMap<>();


        // ---------------------------------------------
        // LOGIN REQUIRED
        // ---------------------------------------------

        String normalizedEmail =
                normalizeEmail(
                        userEmail
                );


        if (
                normalizedEmail == null
        ) {

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


        // ---------------------------------------------
        // ALREADY REVIEWED
        // ---------------------------------------------

        Optional<Review> existingReview =
                reviewRepository
                        .findByProductIdAndUserEmail(
                                productId,
                                normalizedEmail
                        );


        if (
                existingReview.isPresent()
        ) {

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
                    true
            );


            result.put(
                    "delivered",
                    true
            );


            result.put(
                    "existingReviewId",
                    review.getId()
            );


            return result;
        }


        // ---------------------------------------------
        // PURCHASE STATUS
        // ---------------------------------------------

        Map<String, Boolean> purchaseStatus =
                checkPurchaseStatus(
                        productId,
                        normalizedEmail
                );


        boolean purchased =
                Boolean.TRUE.equals(
                        purchaseStatus.get(
                                "purchased"
                        )
                );


        boolean delivered =
                Boolean.TRUE.equals(
                        purchaseStatus.get(
                                "delivered"
                        )
                );


        // ---------------------------------------------
        // NOT PURCHASED
        // ---------------------------------------------

        if (
                !purchased
        ) {

            result.put(
                    "canReview",
                    false
            );


            result.put(
                    "reason",
                    "PURCHASE_REQUIRED"
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


        // ---------------------------------------------
        // PURCHASED BUT NOT DELIVERED
        // ---------------------------------------------

        if (
                !delivered
        ) {

            result.put(
                    "canReview",
                    false
            );


            result.put(
                    "reason",
                    "DELIVERY_REQUIRED"
            );


            result.put(
                    "purchased",
                    true
            );


            result.put(
                    "delivered",
                    false
            );


            return result;
        }


        // ---------------------------------------------
        // DELIVERED
        // ---------------------------------------------

        result.put(
                "canReview",
                true
        );


        result.put(
                "reason",
                "VERIFIED_ELIGIBLE"
        );


        result.put(
                "purchased",
                true
        );


        result.put(
                "delivered",
                true
        );


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

        String normalizedEmail =
                normalizeEmail(
                        userEmail
                );


        if (
                normalizedEmail == null
        ) {

            throw new RuntimeException(
                    "User email is required."
            );
        }


        validateRating(
                rating
        );


        String cleanedComment =
                validateComment(
                        comment
                );


        Optional<Review> optionalReview =
                reviewRepository.findById(
                        reviewId
                );


        if (
                optionalReview.isEmpty()
        ) {

            throw new RuntimeException(
                    "Review not found."
            );
        }


        Review review =
                optionalReview.get();


        String reviewEmail =
                normalizeEmail(
                        review.getUserEmail()
                );


        if (
                reviewEmail == null ||
                !reviewEmail.equals(
                        normalizedEmail
                )
        ) {

            throw new RuntimeException(
                    "You can edit only your own review."
            );
        }


        review.setRating(
                rating
        );


        review.setComment(
                cleanedComment
        );


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

        String normalizedEmail =
                normalizeEmail(
                        userEmail
                );


        if (
                normalizedEmail == null
        ) {

            throw new RuntimeException(
                    "User email is required."
            );
        }


        Optional<Review> optionalReview =
                reviewRepository.findById(
                        reviewId
                );


        if (
                optionalReview.isEmpty()
        ) {

            return false;
        }


        Review review =
                optionalReview.get();


        String reviewEmail =
                normalizeEmail(
                        review.getUserEmail()
                );


        if (
                reviewEmail == null ||
                !reviewEmail.equals(
                        normalizedEmail
                )
        ) {

            throw new RuntimeException(
                    "You can delete only your own review."
            );
        }


        reviewRepository.delete(
                review
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

        if (
                id == null
        ) {

            return false;
        }


        if (
                !reviewRepository.existsById(
                        id
                )
        ) {

            return false;
        }


        reviewRepository.deleteById(
                id
        );


        return true;
    }


    // =====================================================
    // GET TOP RATED PRODUCTS
    // =====================================================

    public List<Map<String, Object>>
    getTopRatedProducts() {

        List<Product> products =
                productRepository.findAll();


        List<Review> reviews =
                reviewRepository.findAll();


        if (
                products.isEmpty() ||
                reviews.isEmpty()
        ) {

            return List.of();
        }


        Map<Long, Product> productMap =
                new HashMap<>();


        for (
                Product product :
                products
        ) {

            if (
                    product.getId() != null
            ) {

                productMap.put(
                        product.getId(),
                        product
                );

            }

        }


        class RatingGroup {

            String name;

            String category;

            double totalRating;

            long reviewCount;

            List<Product> variants =
                    new java.util.ArrayList<>();

        }


        Map<String, RatingGroup> groups =
                new HashMap<>();


        for (
                Review review :
                reviews
        ) {

            if (
                    review.getProductId() == null ||
                    review.getRating() == null ||
                    review.getRating() < 1 ||
                    review.getRating() > 5
            ) {

                continue;
            }


            Product product =
                    productMap.get(
                            review.getProductId()
                    );


            if (
                    product == null
            ) {

                continue;
            }


            String name =
                    String.valueOf(
                            product.getName() == null
                                    ? ""
                                    : product.getName()
                    )
                    .trim()
                    .toLowerCase();


            String category =
                    String.valueOf(
                            product.getCategory() == null
                                    ? ""
                                    : product.getCategory()
                    )
                    .trim()
                    .toLowerCase();


            String groupKey =
                    name +
                    "__" +
                    category;


            RatingGroup group =
                    groups.get(
                            groupKey
                    );


            if (
                    group == null
            ) {

                group =
                        new RatingGroup();


                group.name =
                        product.getName();


                group.category =
                        product.getCategory();


                groups.put(
                        groupKey,
                        group
                );

            }


            group.totalRating +=
                    review.getRating();


            group.reviewCount++;


            boolean alreadyAdded =
                    group.variants
                            .stream()
                            .anyMatch(
                                    variant ->
                                            variant.getId()
                                                    .equals(
                                                            product.getId()
                                                    )
                            );


            if (
                    !alreadyAdded
            ) {

                group.variants.add(
                        product
                );

            }

        }


        List<Map<String, Object>> result =
                new java.util.ArrayList<>();


        for (
                RatingGroup group :
                groups.values()
        ) {

            if (
                    group.reviewCount <= 0
            ) {

                continue;
            }


            double average =
                    group.totalRating /
                    group.reviewCount;


            average =
                    Math.round(
                            average * 10.0
                    ) / 10.0;


            group.variants.sort(
                    (a, b) -> {

                        String aSize =
                                a.getSize() == null
                                        ? ""
                                        : a.getSize();


                        String bSize =
                                b.getSize() == null
                                        ? ""
                                        : b.getSize();


                        return aSize.compareToIgnoreCase(
                                bSize
                        );

                    }
            );


            Map<String, Object> item =
                    new HashMap<>();


            item.put(
                    "name",
                    group.name
            );


            item.put(
                    "category",
                    group.category
            );


            item.put(
                    "averageRating",
                    average
            );


            item.put(
                    "reviewCount",
                    group.reviewCount
            );


            item.put(
                    "variants",
                    group.variants
            );


            result.add(
                    item
            );

        }


        result.sort(
                (a, b) -> {

                    double ratingA =
                            ((Number)
                                    a.get(
                                            "averageRating"
                                    ))
                                    .doubleValue();


                    double ratingB =
                            ((Number)
                                    b.get(
                                            "averageRating"
                                    ))
                                    .doubleValue();


                    int comparison =
                            Double.compare(
                                    ratingB,
                                    ratingA
                            );


                    if (
                            comparison != 0
                    ) {

                        return comparison;
                    }


                    long countA =
                            ((Number)
                                    a.get(
                                            "reviewCount"
                                    ))
                                    .longValue();


                    long countB =
                            ((Number)
                                    b.get(
                                            "reviewCount"
                                    ))
                                    .longValue();


                    return Long.compare(
                            countB,
                            countA
                    );

                }
        );


        return result
                .stream()
                .limit(6)
                .toList();
    }

}