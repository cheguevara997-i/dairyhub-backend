package dairyhub_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dairyhub_backend.entity.Subscription;
import dairyhub_backend.service.SubscriptionService;

@RestController
@RequestMapping("/api/subscriptions")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://dairyhub-five.vercel.app"
})
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(
            SubscriptionService subscriptionService) {

        this.subscriptionService = subscriptionService;
    }

    // =========================================
    // CREATE NEW SUBSCRIPTION
    // =========================================

    @PostMapping
    public ResponseEntity<Subscription> createSubscription(
            @RequestBody Subscription subscription) {

        Subscription savedSubscription =
                subscriptionService.createSubscription(
                        subscription
                );

        return ResponseEntity.ok(savedSubscription);
    }

    // =========================================
    // SAVE PAID SUBSCRIPTION
    // =========================================

    @PostMapping("/paid")
    public ResponseEntity<Subscription> savePaidSubscription(
            @RequestBody Subscription subscription) {

        Subscription savedSubscription =
                subscriptionService.savePaidSubscription(
                        subscription
                );

        return ResponseEntity.ok(savedSubscription);
    }

    // =========================================
    // GET ALL SUBSCRIPTIONS
    // =========================================

    @GetMapping
    public ResponseEntity<List<Subscription>>
            getAllSubscriptions() {

        return ResponseEntity.ok(
                subscriptionService.getAllSubscriptions()
        );
    }

    // =========================================
    // GET CUSTOMER SUBSCRIPTIONS
    // =========================================

    @GetMapping("/customer/{email}")
    public ResponseEntity<List<Subscription>>
            getSubscriptionsByCustomerEmail(
                    @PathVariable String email) {

        return ResponseEntity.ok(
                subscriptionService
                        .getSubscriptionsByCustomerEmail(email)
        );
    }

    // =========================================
    // GET SUBSCRIPTION BY ID
    // =========================================

    @GetMapping("/{id}")
    public ResponseEntity<Subscription>
            getSubscriptionById(
                    @PathVariable Long id) {

        Subscription subscription =
                subscriptionService.getSubscriptionById(id);

        if (subscription == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(subscription);
    }

    // =========================================
    // UPDATE SUBSCRIPTION
    // =========================================

    @PutMapping("/{id}")
    public ResponseEntity<Subscription>
            updateSubscription(
                    @PathVariable Long id,
                    @RequestBody Subscription updatedSubscription) {

        Subscription subscription =
                subscriptionService.updateSubscription(
                        id,
                        updatedSubscription
                );

        if (subscription == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(subscription);
    }

    // =========================================
    // PAUSE SUBSCRIPTION
    // =========================================

    @PutMapping("/{id}/pause")
    public ResponseEntity<Subscription>
            pauseSubscription(
                    @PathVariable Long id) {

        Subscription subscription =
                subscriptionService.pauseSubscription(id);

        if (subscription == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(subscription);
    }

    // =========================================
    // RESUME SUBSCRIPTION
    // =========================================

    @PutMapping("/{id}/resume")
    public ResponseEntity<Subscription>
            resumeSubscription(
                    @PathVariable Long id) {

        Subscription subscription =
                subscriptionService.resumeSubscription(id);

        if (subscription == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(subscription);
    }

    // =========================================
    // CANCEL SUBSCRIPTION
    // =========================================

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Subscription>
            cancelSubscription(
                    @PathVariable Long id) {

        Subscription subscription =
                subscriptionService.cancelSubscription(id);

        if (subscription == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(subscription);
    }

    // =========================================
    // DELETE SUBSCRIPTION
    // =========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>
            deleteSubscription(
                    @PathVariable Long id) {

        boolean deleted =
                subscriptionService.deleteSubscription(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}