package dairyhub_backend.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dairyhub_backend.service.PaymentService;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "http://localhost:5173")
public class PaymentController {

    private final PaymentService paymentService;


    public PaymentController(
            PaymentService paymentService) {

        this.paymentService =
                paymentService;
    }


    // =========================================
    // CREATE RAZORPAY ORDER
    // =========================================

    @PostMapping("/create-order")
    public ResponseEntity<?> createPaymentOrder(
            @RequestBody Map<String, Object> request) {

        try {

            Object amountObject =
                    request.get("amount");

            if (amountObject == null) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "error",
                                        "Amount is required"
                                )
                        );
            }


            Double amount =
                    Double.parseDouble(
                            amountObject.toString()
                    );


            if (amount <= 0) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "error",
                                        "Amount must be greater than zero"
                                )
                        );
            }


            String receipt =
                    "DH_" +
                    System.currentTimeMillis();


            Map<String, Object> response =
                    paymentService
                            .createRazorpayOrder(
                                    amount,
                                    receipt
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
                                    "error",
                                    "Unable to create payment order"
                            )
                    );

        }

    }


    // =========================================
    // VERIFY PAYMENT
    // =========================================

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(
            @RequestBody Map<String, String> request) {

        try {

            String razorpayOrderId =
                    request.get(
                            "razorpay_order_id"
                    );

            String razorpayPaymentId =
                    request.get(
                            "razorpay_payment_id"
                    );

            String razorpaySignature =
                    request.get(
                            "razorpay_signature"
                    );


            if (
                    razorpayOrderId == null ||
                    razorpayPaymentId == null ||
                    razorpaySignature == null
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
                            razorpayOrderId,
                            razorpayPaymentId,
                            razorpaySignature
                    );


            if (!verified) {

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(
                                Map.of(
                                        "success",
                                        false,
                                        "message",
                                        "Payment verification failed"
                                )
                        );
            }


            return ResponseEntity.ok(
                    Map.of(
                            "success",
                            true,
                            "message",
                            "Payment verified successfully"
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
                                    "Unable to verify payment"
                            )
                    );

        }

    }

}