package dairyhub_backend.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dairyhub_backend.service.SubscriptionPaymentService;

@RestController
@RequestMapping("/api/subscription-payment")
@CrossOrigin(origins = "http://localhost:5173")
public class SubscriptionPaymentController {

    private final SubscriptionPaymentService paymentService;


    public SubscriptionPaymentController(
            SubscriptionPaymentService paymentService) {

        this.paymentService =
                paymentService;
    }


    // =========================================
    // CREATE RAZORPAY SUBSCRIPTION
    // =========================================

    @PostMapping("/create")
    public ResponseEntity<?> createSubscription(
            @RequestBody Map<String, String> request) {

        try {

            String milkType =
                    request.get("milkType");

            String quantity =
                    request.get("quantity");

            String duration =
                    request.get("duration");


            if (
                    milkType == null ||
                    quantity == null ||
                    duration == null
            ) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "success",
                                        false,
                                        "message",
                                        "Subscription details are required"
                                )
                        );

            }


            Map<String, Object> response =
                    paymentService
                            .createSubscription(
                                    milkType,
                                    quantity,
                                    duration
                            );


            response.put(
                    "success",
                    true
            );


            return ResponseEntity.ok(
                    response
            );


        } catch (Exception e) {

            e.printStackTrace();


            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body(
                            Map.of(
                                    "success",
                                    false,
                                    "message",
                                    "Unable to create Razorpay subscription"
                            )
                    );

        }

    }


    // =========================================
    // VERIFY SUBSCRIPTION PAYMENT
    // =========================================

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(
            @RequestBody Map<String, String> request) {

        try {

            String paymentId =
                    request.get(
                            "razorpay_payment_id"
                    );

            String subscriptionId =
                    request.get(
                            "razorpay_subscription_id"
                    );

            String signature =
                    request.get(
                            "razorpay_signature"
                    );


            if (
                    paymentId == null ||
                    subscriptionId == null ||
                    signature == null
            ) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "success",
                                        false,
                                        "message",
                                        "Payment details are missing"
                                )
                        );

            }


            boolean verified =
                    paymentService.verifyPayment(
                            paymentId,
                            subscriptionId,
                            signature
                    );


            if (!verified) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "success",
                                        false,
                                        "message",
                                        "Subscription payment verification failed"
                                )
                        );

            }


            return ResponseEntity.ok(
                    Map.of(
                            "success",
                            true,
                            "message",
                            "Subscription payment verified successfully"
                    )
            );


        } catch (Exception e) {

            e.printStackTrace();


            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body(
                            Map.of(
                                    "success",
                                    false,
                                    "message",
                                    "Unable to verify subscription payment"
                            )
                    );

        }

    }

}