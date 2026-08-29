package dairyhub_backend.service;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;

@Service
public class PaymentService {

    private final String keyId;
    private final String keySecret;

    public PaymentService(
            @Value("${razorpay.key.id}") String keyId,
            @Value("${razorpay.key.secret}") String keySecret) {

        this.keyId = keyId;
        this.keySecret = keySecret;
    }


    // Create Razorpay order
    public Map<String, Object> createRazorpayOrder(
            Double amount,
            String receipt) throws Exception {

        RazorpayClient razorpayClient =
                new RazorpayClient(
                        keyId,
                        keySecret
                );


        // Razorpay expects amount in paise
        int amountInPaise =
                (int) Math.round(amount * 100);


        JSONObject orderRequest =
                new JSONObject();

        orderRequest.put(
                "amount",
                amountInPaise
        );

        orderRequest.put(
                "currency",
                "INR"
        );

        orderRequest.put(
                "receipt",
                receipt
        );


        Order razorpayOrder =
                razorpayClient.orders.create(
                        orderRequest
                );


        Map<String, Object> response =
                new HashMap<>();


        response.put(
                "id",
                razorpayOrder.get("id")
        );

        response.put(
                "amount",
                razorpayOrder.get("amount")
        );

        response.put(
                "currency",
                razorpayOrder.get("currency")
        );

        response.put(
                "key",
                keyId
        );


        return response;
    }


    // Verify Razorpay payment signature
    public boolean verifyPayment(
            String razorpayOrderId,
            String razorpayPaymentId,
            String razorpaySignature) {

        try {

            JSONObject options =
                    new JSONObject();

            options.put(
                    "razorpay_order_id",
                    razorpayOrderId
            );

            options.put(
                    "razorpay_payment_id",
                    razorpayPaymentId
            );

            options.put(
                    "razorpay_signature",
                    razorpaySignature
            );


            return Utils.verifyPaymentSignature(
                    options,
                    keySecret
            );

        } catch (Exception e) {

            return false;

        }

    }

}