package dairyhub_backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dairyhub_backend.entity.Review;
import dairyhub_backend.service.ReviewService;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://dairyhub-five.vercel.app"
})
public class ReviewController {

    private final ReviewService reviewService;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }


    // =====================================================
    // ADD REVIEW
    // =====================================================

    @PostMapping
    public ResponseEntity<?> addReview(
            @RequestBody Map<String, Object> request) {

        try {

            Long productId =
                    Long.valueOf(
                            request.get("productId").toString()
                    );


            String productName =
                    request.get("productName") != null
                            ? request.get("productName").toString()
                            : "";


            String userEmail =
                    request.get("userEmail") != null
                            ? request.get("userEmail").toString()
                            : "";


            String userName =
                    request.get("userName") != null
                            ? request.get("userName").toString()
                            : "";


            Integer rating =
                    Integer.valueOf(
                            request.get("rating").toString()
                    );


            String comment =
                    request.get("comment") != null
                            ? request.get("comment").toString()
                            : "";


            Review review =
                    reviewService.addReview(
                            productId,
                            productName,
                            userEmail,
                            userName,
                            rating,
                            comment
                    );


            return ResponseEntity.ok(review);


        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());


        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Unable to submit review."
                    );
        }
    }


    // =====================================================
    // GET PRODUCT REVIEWS
    // =====================================================

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getProductReviews(
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                reviewService.getProductReviews(
                        productId
                )
        );
    }


    // =====================================================
    // GET PRODUCT RATING SUMMARY
    // =====================================================

    @GetMapping("/product/{productId}/summary")
    public ResponseEntity<Map<String, Object>> getRatingSummary(
            @PathVariable Long productId) {

        Double averageRating =
                reviewService.getAverageRating(
                        productId
                );


        long reviewCount =
                reviewService.getReviewCount(
                        productId
                );


        Map<String, Object> response =
                new HashMap<>();


        response.put(
                "averageRating",
                averageRating
        );


        response.put(
                "reviewCount",
                reviewCount
        );


        return ResponseEntity.ok(response);
    }


    // =====================================================
    // CHECK REVIEW ELIGIBILITY
    // =====================================================

    @GetMapping("/product/{productId}/eligibility")
    public ResponseEntity<Map<String, Object>>
    checkReviewEligibility(
            @PathVariable Long productId,
            @RequestParam String userEmail) {

        return ResponseEntity.ok(
                reviewService.getReviewEligibility(
                        productId,
                        userEmail
                )
        );
    }


    // =====================================================
    // UPDATE CUSTOMER REVIEW
    // =====================================================

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {

        try {

            String userEmail =
                    request.get("userEmail") != null
                            ? request.get("userEmail").toString()
                            : "";


            Integer rating =
                    Integer.valueOf(
                            request.get("rating").toString()
                    );


            String comment =
                    request.get("comment") != null
                            ? request.get("comment").toString()
                            : "";


            Review review =
                    reviewService.updateReview(
                            id,
                            userEmail,
                            rating,
                            comment
                    );


            return ResponseEntity.ok(review);


        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());


        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Unable to update review."
                    );
        }
    }


    // =====================================================
    // GET ALL REVIEWS
    // ADMIN
    // =====================================================

    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {

        return ResponseEntity.ok(
                reviewService.getAllReviews()
        );
    }


    // =====================================================
    // DELETE REVIEW
    // ADMIN
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(
            @PathVariable Long id) {

        boolean deleted =
                reviewService.deleteReview(id);


        if (!deleted) {

            return ResponseEntity
                    .notFound()
                    .build();
        }


        return ResponseEntity.ok(
                "Review deleted successfully."
        );
    }


    // =====================================================
    // DELETE CUSTOMER'S OWN REVIEW
    // =====================================================

    @DeleteMapping("/{id}/user")
    public ResponseEntity<?> deleteOwnReview(
            @PathVariable Long id,
            @RequestParam String userEmail) {

        try {

            boolean deleted =
                    reviewService.deleteOwnReview(
                            id,
                            userEmail
                    );


            if (!deleted) {

                return ResponseEntity
                        .notFound()
                        .build();
            }


            return ResponseEntity.ok(
                    "Review deleted successfully."
            );


        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }
}