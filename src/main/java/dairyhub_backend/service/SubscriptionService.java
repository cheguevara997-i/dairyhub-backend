package dairyhub_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import dairyhub_backend.entity.Subscription;
import dairyhub_backend.repository.SubscriptionRepository;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;


    public SubscriptionService(
            SubscriptionRepository subscriptionRepository) {

        this.subscriptionRepository =
                subscriptionRepository;
    }


    // =========================================
    // CREATE SUBSCRIPTION
    // =========================================

    public Subscription createSubscription(
            Subscription subscription) {

        return subscriptionRepository.save(
                subscription
        );
    }


    // =========================================
    // SAVE PAID SUBSCRIPTION
    // =========================================

    public Subscription savePaidSubscription(
            Subscription subscription) {

        /*
         * This method is called only after
         * Razorpay payment/authorization has
         * been successfully verified.
         */

        subscription.setStatus("ACTIVE");

        subscription.setPaymentStatus("PAID");


        return subscriptionRepository.save(
                subscription
        );
    }


    // =========================================
    // GET ALL SUBSCRIPTIONS
    // =========================================

    public List<Subscription> getAllSubscriptions() {

        return subscriptionRepository.findAll();
    }


    // =========================================
    // GET CUSTOMER SUBSCRIPTIONS
    // =========================================

    public List<Subscription>
            getSubscriptionsByCustomerEmail(
                    String customerEmail) {

        return subscriptionRepository
                .findByCustomerEmail(customerEmail);
    }


    // =========================================
    // GET SUBSCRIPTION BY ID
    // =========================================

    public Subscription getSubscriptionById(
            Long id) {

        return subscriptionRepository
                .findById(id)
                .orElse(null);
    }


    // =========================================
    // UPDATE SUBSCRIPTION
    // =========================================

    public Subscription updateSubscription(
            Long id,
            Subscription updatedSubscription) {

        Subscription existingSubscription =
                subscriptionRepository
                        .findById(id)
                        .orElse(null);


        if (existingSubscription == null) {

            return null;

        }


        existingSubscription.setCustomerEmail(
                updatedSubscription
                        .getCustomerEmail()
        );


        existingSubscription.setMilkType(
                updatedSubscription
                        .getMilkType()
        );


        existingSubscription.setQuantity(
                updatedSubscription
                        .getQuantity()
        );


        existingSubscription.setDuration(
                updatedSubscription
                        .getDuration()
        );


        existingSubscription.setDeliveryTime(
                updatedSubscription
                        .getDeliveryTime()
        );


        return subscriptionRepository.save(
                existingSubscription
        );
    }


    // =========================================
    // PAUSE SUBSCRIPTION
    // =========================================

    public Subscription pauseSubscription(
            Long id) {

        Subscription subscription =
                subscriptionRepository
                        .findById(id)
                        .orElse(null);


        if (subscription == null) {

            return null;

        }


        subscription.setStatus(
                "PAUSED"
        );


        return subscriptionRepository.save(
                subscription
        );
    }


    // =========================================
    // RESUME SUBSCRIPTION
    // =========================================

    public Subscription resumeSubscription(
            Long id) {

        Subscription subscription =
                subscriptionRepository
                        .findById(id)
                        .orElse(null);


        if (subscription == null) {

            return null;

        }


        subscription.setStatus(
                "ACTIVE"
        );


        return subscriptionRepository.save(
                subscription
        );
    }


    // =========================================
    // CANCEL SUBSCRIPTION
    // =========================================

    public Subscription cancelSubscription(
            Long id) {

        Subscription subscription =
                subscriptionRepository
                        .findById(id)
                        .orElse(null);


        if (subscription == null) {

            return null;

        }


        subscription.setStatus(
                "CANCELLED"
        );


        return subscriptionRepository.save(
                subscription
        );
    }


    // =========================================
    // DELETE SUBSCRIPTION
    // =========================================

    public boolean deleteSubscription(
            Long id) {

        if (
                !subscriptionRepository
                        .existsById(id)
        ) {

            return false;

        }


        subscriptionRepository.deleteById(
                id
        );


        return true;
    }

}