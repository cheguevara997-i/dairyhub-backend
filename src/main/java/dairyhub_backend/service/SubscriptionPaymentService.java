package dairyhub_backend.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.razorpay.Plan;
import com.razorpay.RazorpayClient;
import com.razorpay.Subscription;

@Service
public class SubscriptionPaymentService {

    private final String keyId;
    private final String keySecret;


    public SubscriptionPaymentService(
            @Value("${razorpay.key.id}") String keyId,
            @Value("${razorpay.key.secret}") String keySecret) {

        this.keyId = keyId;
        this.keySecret = keySecret;
    }


    // =========================================
    // CREATE RAZORPAY PLAN + SUBSCRIPTION
    // =========================================

    public Map<String, Object> createSubscription(
            String milkType,
            String quantity,
            String duration) throws Exception {


        double litres =
                extractLitres(quantity);


        if (litres <= 0) {

            throw new IllegalArgumentException(
                    "Invalid quantity"
            );

        }


        double pricePerLitre;

        if ("Cow Milk".equalsIgnoreCase(milkType)) {

            pricePerLitre = 60.0;

        } else if (
                "Buffalo Milk".equalsIgnoreCase(
                        milkType)) {

            pricePerLitre = 70.0;

        } else {

            throw new IllegalArgumentException(
                    "Invalid milk type"
            );

        }


        /*
         * These are example DairyHub rates.
         *
         * Cow Milk   = ₹60 / litre
         * Buffalo    = ₹70 / litre
         *
         * Change these values later if required.
         */


        double amount;


        String period;

        int interval = 1;

        int totalCount;


        if ("Weekly".equalsIgnoreCase(duration)) {

            period = "weekly";

            amount =
                    pricePerLitre *
                    litres *
                    7;

            totalCount = 52;

        } else {

            period = "monthly";

            amount =
                    pricePerLitre *
                    litres *
                    30;

            totalCount = 12;

        }


        int amountInPaise =
                (int) Math.round(
                        amount * 100
                );


        RazorpayClient client =
                new RazorpayClient(
                        keyId,
                        keySecret
                );


        // =====================================
        // CREATE PLAN
        // =====================================

        JSONObject planRequest =
                new JSONObject();


        planRequest.put(
                "period",
                period
        );


        planRequest.put(
                "interval",
                interval
        );


        JSONObject item =
                new JSONObject();


        item.put(
                "name",
                milkType +
                " - " +
                litres +
                " L"
        );


        item.put(
                "amount",
                amountInPaise
        );


        item.put(
                "currency",
                "INR"
        );


        item.put(
                "description",
                "DairyHub " +
                milkType +
                " subscription"
        );


        planRequest.put(
                "item",
                item
        );


        Plan plan =
                client.plans.create(
                        planRequest
                );


        String planId =
                plan.get("id");


        // =====================================
        // CREATE SUBSCRIPTION
        // =====================================

        JSONObject subscriptionRequest =
                new JSONObject();


        subscriptionRequest.put(
                "plan_id",
                planId
        );


        subscriptionRequest.put(
                "total_count",
                totalCount
        );


        subscriptionRequest.put(
                "quantity",
                1
        );


        subscriptionRequest.put(
                "customer_notify",
                true
        );


        Subscription razorpaySubscription =
                client.subscriptions.create(
                        subscriptionRequest
                );


        String subscriptionId =
                razorpaySubscription.get("id");


        Map<String, Object> response =
                new HashMap<>();


        response.put(
                "key",
                keyId
        );


        response.put(
                "planId",
                planId
        );


        response.put(
                "subscriptionId",
                subscriptionId
        );


        response.put(
                "amount",
                amount
        );


        response.put(
                "duration",
                duration
        );


        response.put(
                "milkType",
                milkType
        );


        response.put(
                "quantity",
                litres
        );


        return response;
    }


    // =========================================
    // EXTRACT NUMBER FROM QUANTITY
    // Example:
    // "2 Litres" -> 2
    // "1 Litre"  -> 1
    // =========================================

    private double extractLitres(
            String quantity) {

        if (quantity == null) {

            return 0;

        }


        String number =
                quantity
                        .trim()
                        .split("\\s+")[0];


        try {

            return Double.parseDouble(
                    number
            );

        } catch (NumberFormatException e) {

            return 0;

        }

    }


    // =========================================
    // VERIFY SUBSCRIPTION PAYMENT
    // =========================================

    public boolean verifyPayment(
            String paymentId,
            String subscriptionId,
            String signature) {

        try {

            String payload =
                    paymentId +
                    "|" +
                    subscriptionId;


            byte[] secretBytes =
                    keySecret.getBytes(
                            StandardCharsets.UTF_8
                    );


            byte[] payloadBytes =
                    payload.getBytes(
                            StandardCharsets.UTF_8
                    );


            javax.crypto.Mac mac =
                    javax.crypto.Mac.getInstance(
                            "HmacSHA256"
                    );


            javax.crypto.spec.SecretKeySpec key =
                    new javax.crypto.spec.SecretKeySpec(
                            secretBytes,
                            "HmacSHA256"
                    );


            mac.init(key);


            byte[] digest =
                    mac.doFinal(
                            payloadBytes
                    );


            StringBuilder hex =
                    new StringBuilder();


            for (byte b : digest) {

                hex.append(
                        String.format(
                                "%02x",
                                b
                        )
                );

            }


            return MessageDigest.isEqual(
                    hex.toString()
                            .getBytes(
                                    StandardCharsets.UTF_8
                            ),
                    signature.getBytes(
                            StandardCharsets.UTF_8
                    )
            );


        } catch (Exception e) {

            return false;

        }

    }

}