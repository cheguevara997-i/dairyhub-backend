package dairyhub_backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dairyhub_backend.entity.Review;
import dairyhub_backend.service.JwtService;
import dairyhub_backend.service.ReviewService;


@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://dairyhub-five.vercel.app"
})
public class ReviewController {


    private final ReviewService reviewService;

    private final JwtService jwtService;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public ReviewController(
            ReviewService reviewService,
            JwtService jwtService) {

        this.reviewService =
                reviewService;

        this.jwtService =
                jwtService;
    }


    // =====================================================
    // EXTRACT BEARER TOKEN
    // =====================================================

    private String extractToken(
            String authorizationHeader) {

        if (
                authorizationHeader == null ||
                authorizationHeader.trim().isEmpty()
        ) {

            return null;
        }


        if (
                !authorizationHeader
                        .startsWith("Bearer ")
        ) {

            return null;
        }


        String token =
                authorizationHeader
                        .substring(7)
                        .trim();


        if (
                token.isEmpty()
        ) {

            return null;
        }


        return token;
    }


    // =====================================================
    // CHECK VALID TOKEN
    // =====================================================

    private boolean isAuthenticated(
            String authorizationHeader) {

        String token =
                extractToken(
                        authorizationHeader
                );


        if (
                token == null
        ) {

            return false;
        }


        return jwtService.isValidToken(
                token
        );
    }


    // =====================================================
    // GET EMAIL FROM TOKEN
    // =====================================================

    private String getEmailFromToken(
            String authorizationHeader) {

        String token =
                extractToken(
                        authorizationHeader
                );


        if (
                token == null
        ) {

            return null;
        }


        try {

            String email =
                    jwtService
                            .getClaims(token)
                            .get(
                                    "email",
                                    String.class
                            );


            if (
                    email == null ||
                    email.trim().isEmpty()
            ) {

                return null;
            }


            return email
                    .trim()
                    .toLowerCase();


        } catch (Exception e) {

            return null;
        }
    }


    // =====================================================
    // CHECK ADMIN
    // =====================================================

    private boolean isAdmin(
            String authorizationHeader) {

        String token =
                extractToken(
                        authorizationHeader
                );


        if (
                token == null
        ) {

            return false;
        }


        return jwtService.isValidToken(
                token
        ) &&
        jwtService.isAdmin(
                token
        );
    }


    // =====================================================
    // ADD REVIEW
    // =====================================================

    @PostMapping
    public ResponseEntity<?> addReview(
            @RequestBody Map<String, Object> request,
            @RequestHeader(
                    value = "Authorization",
                    required = false
            )
            String authorizationHeader) {

        try {

            // ---------------------------------------------
            // AUTHENTICATION
            // ---------------------------------------------

            if (
                    !isAuthenticated(
                            authorizationHeader
                    )
            ) {

                return ResponseEntity
                        .status(
                                HttpStatus.UNAUTHORIZED
                        )
                        .body(
                                Map.of(
                                        "success",
                                        false,

                                        "message",
                                        "Please login before submitting a review."
                                )
                        );
            }


            // ---------------------------------------------
            // GET EMAIL FROM JWT
            // ---------------------------------------------

            String userEmail =
                    getEmailFromToken(
                            authorizationHeader
                    );


            if (
                    userEmail == null
            ) {

                return ResponseEntity
                        .status(
                                HttpStatus.UNAUTHORIZED
                        )
                        .body(
                                Map.of(
                                        "success",
                                        false,

                                        "message",
                                        "Unable to identify the logged-in user."
                                )
                        );
            }


            // ---------------------------------------------
            // PRODUCT ID
            // ---------------------------------------------

            if (
                    request.get("productId") == null
            ) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "Product ID is required."
                        );
            }


            Long productId =
                    Long.valueOf(
                            request
                                    .get("productId")
                                    .toString()
                    );


            // ---------------------------------------------
            // PRODUCT NAME
            // ---------------------------------------------

            String productName =
                    request.get("productName") != null

                            ? request
                                    .get("productName")
                                    .toString()

                            : "";


            // ---------------------------------------------
            // USER NAME
            // ---------------------------------------------

            String userName =
                    request.get("userName") != null

                            ? request
                                    .get("userName")
                                    .toString()

                            : "";


            // ---------------------------------------------
            // RATING
            // ---------------------------------------------

            if (
                    request.get("rating") == null
            ) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "Rating is required."
                        );
            }


            Integer rating =
                    Integer.valueOf(
                            request
                                    .get("rating")
                                    .toString()
                    );


            // ---------------------------------------------
            // COMMENT
            // ---------------------------------------------

            String comment =
                    request.get("comment") != null

                            ? request
                                    .get("comment")
                                    .toString()

                            : "";


            // ---------------------------------------------
            // SAVE REVIEW
            // ---------------------------------------------

            Review review =
                    reviewService.addReview(
                            productId,
                            productName,
                            userEmail,
                            userName,
                            rating,
                            comment
                    );


            return ResponseEntity.ok(
                    review
            );


        } catch (
                RuntimeException e
        ) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            e.getMessage()
                    );


        } catch (
                Exception e
        ) {

            e.printStackTrace();


            return ResponseEntity
                    .badRequest()
                    .body(
                            "Unable to submit review."
                    );
        }
    }


    // =====================================================
    // GET PRODUCT REVIEWS
    // PUBLIC
    // =====================================================

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>>
    getProductReviews(
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                reviewService.getProductReviews(
                        productId
                )
        );
    }


    // =====================================================
    // GET PRODUCT RATING SUMMARY
    // PUBLIC
    // =====================================================

    @GetMapping("/product/{productId}/summary")
    public ResponseEntity<Map<String, Object>>
    getRatingSummary(
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


        return ResponseEntity.ok(
                response
        );
    }


    // =====================================================
    // GET TOP RATED PRODUCTS
    // PUBLIC
    // =====================================================

    @GetMapping("/top-rated")
    public ResponseEntity<List<Map<String, Object>>>
    getTopRatedProducts() {

        return ResponseEntity.ok(
                reviewService.getTopRatedProducts()
        );
    }


    // =====================================================
    // CHECK REVIEW ELIGIBILITY
    // AUTHENTICATED CUSTOMER
    // =====================================================

    @GetMapping("/product/{productId}/eligibility")
    public ResponseEntity<?> checkReviewEligibility(
            @PathVariable Long productId,

            @RequestParam(
                    required = false
            )
            String ignoredUserEmail,

            @RequestHeader(
                    value = "Authorization",
                    required = false
            )
            String authorizationHeader) {

        /*
         * IMPORTANT:
         *
         * The userEmail query parameter is intentionally
         * ignored.
         *
         * The backend identifies the customer from
         * the JWT token instead.
         */

        if (
                !isAuthenticated(
                        authorizationHeader
                )
        ) {

            return ResponseEntity
                    .status(
                            HttpStatus.UNAUTHORIZED
                    )
                    .body(
                            Map.of(
                                    "canReview",
                                    false,

                                    "reason",
                                    "LOGIN_REQUIRED",

                                    "purchased",
                                    false,

                                    "delivered",
                                    false,

                                    "message",
                                    "Please login before checking review eligibility."
                            )
                    );
        }


        String userEmail =
                getEmailFromToken(
                        authorizationHeader
                );


        if (
                userEmail == null
        ) {

            return ResponseEntity
                    .status(
                            HttpStatus.UNAUTHORIZED
                    )
                    .body(
                            Map.of(
                                    "canReview",
                                    false,

                                    "reason",
                                    "INVALID_SESSION",

                                    "purchased",
                                    false,

                                    "delivered",
                                    false
                            )
                    );
        }


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

            @RequestBody Map<String, Object> request,

            @RequestHeader(
                    value = "Authorization",
                    required = false
            )
            String authorizationHeader) {

        try {

            // ---------------------------------------------
            // AUTHENTICATION
            // ---------------------------------------------

            if (
                    !isAuthenticated(
                            authorizationHeader
                    )
            ) {

                return ResponseEntity
                        .status(
                                HttpStatus.UNAUTHORIZED
                        )
                        .body(
                                Map.of(
                                        "success",
                                        false,

                                        "message",
                                        "Please login before editing your review."
                                )
                        );
            }


            // ---------------------------------------------
            // GET EMAIL FROM JWT
            // ---------------------------------------------

            String userEmail =
                    getEmailFromToken(
                            authorizationHeader
                    );


            if (
                    userEmail == null
            ) {

                return ResponseEntity
                        .status(
                                HttpStatus.UNAUTHORIZED
                        )
                        .body(
                                Map.of(
                                        "success",
                                        false,

                                        "message",
                                        "Unable to identify the logged-in user."
                                )
                        );
            }


            // ---------------------------------------------
            // RATING
            // ---------------------------------------------

            if (
                    request.get("rating") == null
            ) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "Rating is required."
                        );
            }


            Integer rating =
                    Integer.valueOf(
                            request
                                    .get("rating")
                                    .toString()
                    );


            // ---------------------------------------------
            // COMMENT
            // ---------------------------------------------

            String comment =
                    request.get("comment") != null

                            ? request
                                    .get("comment")
                                    .toString()

                            : "";


            // ---------------------------------------------
            // UPDATE REVIEW
            // ---------------------------------------------

            Review review =
                    reviewService.updateReview(
                            id,
                            userEmail,
                            rating,
                            comment
                    );


            return ResponseEntity.ok(
                    review
            );


        } catch (
                RuntimeException e
        ) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            e.getMessage()
                    );


        } catch (
                Exception e
        ) {

            e.printStackTrace();


            return ResponseEntity
                    .badRequest()
                    .body(
                            "Unable to update review."
                    );
        }
    }


    // =====================================================
    // GET ALL REVIEWS
    // ADMIN ONLY
    // =====================================================

    @GetMapping
    public ResponseEntity<?> getAllReviews(
            @RequestHeader(
                    value = "Authorization",
                    required = false
            )
            String authorizationHeader) {

        if (
                !isAdmin(
                        authorizationHeader
                )
        ) {

            return ResponseEntity
                    .status(
                            HttpStatus.FORBIDDEN
                    )
                    .body(
                            Map.of(
                                    "success",
                                    false,

                                    "message",
                                    "Admin authorization required."
                            )
                    );
        }


        return ResponseEntity.ok(
                reviewService.getAllReviews()
        );
    }


    // =====================================================
    // DELETE REVIEW
    // ADMIN ONLY
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(
            @PathVariable Long id,

            @RequestHeader(
                    value = "Authorization",
                    required = false
            )
            String authorizationHeader) {

        if (
                !isAdmin(
                        authorizationHeader
                )
        ) {

            return ResponseEntity
                    .status(
                            HttpStatus.FORBIDDEN
                    )
                    .body(
                            Map.of(
                                    "success",
                                    false,

                                    "message",
                                    "Admin authorization required."
                            )
                    );
        }


        boolean deleted =
                reviewService.deleteReview(
                        id
                );


        if (
                !deleted
        ) {

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

            @RequestParam(
                    required = false
            )
            String ignoredUserEmail,

            @RequestHeader(
                    value = "Authorization",
                    required = false
            )
            String authorizationHeader) {

        try {

            // ---------------------------------------------
            // AUTHENTICATION
            // ---------------------------------------------

            if (
                    !isAuthenticated(
                            authorizationHeader
                    )
            ) {

                return ResponseEntity
                        .status(
                                HttpStatus.UNAUTHORIZED
                        )
                        .body(
                                Map.of(
                                        "success",
                                        false,

                                        "message",
                                        "Please login before deleting your review."
                                )
                        );
            }


            // ---------------------------------------------
            // GET EMAIL FROM JWT
            // ---------------------------------------------

            String userEmail =
                    getEmailFromToken(
                            authorizationHeader
                    );


            if (
                    userEmail == null
            ) {

                return ResponseEntity
                        .status(
                                HttpStatus.UNAUTHORIZED
                        )
                        .body(
                                Map.of(
                                        "success",
                                        false,

                                        "message",
                                        "Unable to identify the logged-in user."
                                )
                        );
            }


            // ---------------------------------------------
            // DELETE OWN REVIEW
            // ---------------------------------------------

            boolean deleted =
                    reviewService.deleteOwnReview(
                            id,
                            userEmail
                    );


            if (
                    !deleted
            ) {

                return ResponseEntity
                        .notFound()
                        .build();
            }


            return ResponseEntity.ok(
                    "Review deleted successfully."
            );


        } catch (
                RuntimeException e
        ) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            e.getMessage()
                    );
        }
    }

}