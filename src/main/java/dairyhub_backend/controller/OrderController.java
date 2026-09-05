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

import dairyhub_backend.entity.CustomerOrder;
import dairyhub_backend.service.OrderService;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://dairyhub-five.vercel.app"
})
public class OrderController {

    private final OrderService orderService;


    public OrderController(
            OrderService orderService) {

        this.orderService =
                orderService;
    }


    // =========================================
    // CREATE ORDER
    // =========================================

    @PostMapping
    public ResponseEntity<?> createOrder(
            @RequestBody CustomerOrder order) {

        try {

            CustomerOrder savedOrder =
                    orderService.createOrder(
                            order
                    );


            return ResponseEntity.ok(
                    savedOrder
            );


        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            e.getMessage()
                    );

        }

    }


    // =========================================
    // GET ALL ORDERS
    // =========================================

    @GetMapping
    public ResponseEntity<List<CustomerOrder>>
    getAllOrders() {

        return ResponseEntity.ok(
                orderService.getAllOrders()
        );
    }


    // =========================================
    // GET CUSTOMER ORDERS
    // =========================================

    @GetMapping("/customer/{email}")
    public ResponseEntity<List<CustomerOrder>>
    getOrdersByCustomerEmail(
            @PathVariable String email) {

        return ResponseEntity.ok(
                orderService
                        .getOrdersByCustomerEmail(
                                email
                        )
        );
    }


    // =========================================
    // GET ORDER BY ID
    // =========================================

    @GetMapping("/{id}")
    public ResponseEntity<CustomerOrder>
    getOrderById(
            @PathVariable Long id) {

        CustomerOrder order =
                orderService.getOrderById(
                        id
                );


        if (
                order == null
        ) {

            return ResponseEntity
                    .notFound()
                    .build();
        }


        return ResponseEntity.ok(
                order
        );
    }


    // =========================================
    // CHECK EXPERIENCE FEEDBACK ELIGIBILITY
    // =========================================

    @GetMapping(
            "/{id}/experience-eligibility"
    )
    public ResponseEntity<Map<String, Object>>
    checkExperienceEligibility(
            @PathVariable Long id,
            @RequestParam String customerEmail) {

        CustomerOrder order =
                orderService.getOrderById(
                        id
                );


        Map<String, Object> response =
                new HashMap<>();


        if (
                order == null
        ) {

            response.put(
                    "canReview",
                    false
            );


            response.put(
                    "reason",
                    "ORDER_NOT_FOUND"
            );


            return ResponseEntity
                    .status(404)
                    .body(response);
        }


        String status =
                String.valueOf(
                        order.getStatus()
                )
                .trim()
                .toUpperCase();


        boolean belongsToCustomer =
                order.getCustomerEmail() != null
                &&
                order.getCustomerEmail()
                        .trim()
                        .equalsIgnoreCase(
                                customerEmail.trim()
                        );


        if (
                !belongsToCustomer
        ) {

            response.put(
                    "canReview",
                    false
            );


            response.put(
                    "reason",
                    "NOT_YOUR_ORDER"
            );


            return ResponseEntity.ok(
                    response
            );
        }


        if (
                order.getExperienceRating() != null
        ) {

            response.put(
                    "canReview",
                    false
            );


            response.put(
                    "reason",
                    "ALREADY_REVIEWED"
            );


            response.put(
                    "rating",
                    order.getExperienceRating()
            );


            response.put(
                    "feedback",
                    order.getExperienceFeedback()
            );


            response.put(
                    "status",
                    status
            );


            return ResponseEntity.ok(
                    response
            );
        }


        // =====================================
        // DELIVERED
        // =====================================

        if (
                "DELIVERED".equals(
                        status
                )
        ) {

            response.put(
                    "canReview",
                    true
            );


            response.put(
                    "reason",
                    "DELIVERED"
            );


            response.put(
                    "feedbackType",
                    "PRODUCT_EXPERIENCE"
            );


            return ResponseEntity.ok(
                    response
            );
        }


        // =====================================
        // CANCELLED
        // =====================================

        if (
                "CANCELLED".equals(
                        status
                )
        ) {

            response.put(
                    "canReview",
                    true
            );


            response.put(
                    "reason",
                    "CANCELLED"
            );


            response.put(
                    "feedbackType",
                    "ORDER_EXPERIENCE"
            );


            return ResponseEntity.ok(
                    response
            );
        }


        // =====================================
        // NOT ELIGIBLE YET
        // =====================================

        response.put(
                "canReview",
                false
        );


        response.put(
                "reason",
                "WAIT_FOR_DELIVERY_OR_CANCELLATION"
        );


        response.put(
                "status",
                status
        );


        return ResponseEntity.ok(
                response
        );
    }


    // =========================================
    // SUBMIT EXPERIENCE FEEDBACK
    // =========================================

    @PostMapping(
            "/{id}/experience-feedback"
    )
    public ResponseEntity<?> submitExperienceFeedback(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {

        try {

            String customerEmail =
                    request.get(
                            "customerEmail"
                    ) != null

                            ? request.get(
                                    "customerEmail"
                              )
                              .toString()

                            : "";


            Integer rating =
                    request.get(
                            "rating"
                    ) != null

                            ? Integer.valueOf(
                                    request.get(
                                            "rating"
                                    ).toString()
                              )

                            : null;


            String feedback =
                    request.get(
                            "feedback"
                    ) != null

                            ? request.get(
                                    "feedback"
                              )
                              .toString()

                            : "";


            CustomerOrder updatedOrder =
                    orderService
                            .submitExperienceFeedback(
                                    id,
                                    customerEmail,
                                    rating,
                                    feedback
                            );


            return ResponseEntity.ok(
                    updatedOrder
            );


        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            e.getMessage()
                    );


        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Unable to submit order experience feedback."
                    );
        }
    }


    // =========================================
    // UPDATE ORDER
    // =========================================

    @PutMapping("/{id}")
    public ResponseEntity<CustomerOrder>
    updateOrder(
            @PathVariable Long id,
            @RequestBody CustomerOrder updatedOrder) {

        CustomerOrder order =
                orderService.updateOrder(
                        id,
                        updatedOrder
                );


        if (
                order == null
        ) {

            return ResponseEntity
                    .notFound()
                    .build();
        }


        return ResponseEntity.ok(
                order
        );
    }


    // =========================================
    // DELETE ORDER
    // =========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>
    deleteOrder(
            @PathVariable Long id) {

        boolean deleted =
                orderService.deleteOrder(
                        id
                );


        if (
                !deleted
        ) {

            return ResponseEntity
                    .notFound()
                    .build();
        }


        return ResponseEntity
                .noContent()
                .build();
    }

}