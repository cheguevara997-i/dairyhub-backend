package dairyhub_backend.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================
    // CUSTOMER DETAILS
    // =========================================

    private String customerName;

    private String customerEmail;

    private String phone;

    private String address;

    private String city;

    private String state;

    private String pincode;


    // =========================================
    // SUBSCRIPTION DETAILS
    // =========================================

    private String milkType;

    private String quantity;

    private String duration;

    private String deliveryTime;

    private LocalDate startDate;

    private LocalDate nextDeliveryDate;

    private String status;

    private LocalDateTime createdAt;


    // =========================================
    // RAZORPAY PAYMENT DETAILS
    // =========================================

    private String paymentStatus;

    private String razorpayPlanId;

    private String razorpaySubscriptionId;

    private String razorpayPaymentId;

    private String razorpaySignature;


    // =========================================
    // CONSTRUCTOR
    // =========================================

    public Subscription() {
    }


    // =========================================
    // CREATE DEFAULT DATA
    // =========================================

    @PrePersist
    public void createSubscriptionData() {

        createdAt = LocalDateTime.now();

        if (status == null) {
            status = "PENDING";
        }

        if (paymentStatus == null) {
            paymentStatus = "PENDING";
        }

        if (startDate == null) {
            startDate = LocalDate.now();
        }

        if (nextDeliveryDate == null) {
            nextDeliveryDate = startDate;
        }
    }


    // =========================================
    // ID
    // =========================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    // =========================================
    // CUSTOMER NAME
    // =========================================

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(
            String customerName) {

        this.customerName = customerName;
    }


    // =========================================
    // CUSTOMER EMAIL
    // =========================================

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(
            String customerEmail) {

        this.customerEmail = customerEmail;
    }


    // =========================================
    // PHONE
    // =========================================

    public String getPhone() {
        return phone;
    }

    public void setPhone(
            String phone) {

        this.phone = phone;
    }


    // =========================================
    // ADDRESS
    // =========================================

    public String getAddress() {
        return address;
    }

    public void setAddress(
            String address) {

        this.address = address;
    }


    // =========================================
    // CITY
    // =========================================

    public String getCity() {
        return city;
    }

    public void setCity(
            String city) {

        this.city = city;
    }


    // =========================================
    // STATE
    // =========================================

    public String getState() {
        return state;
    }

    public void setState(
            String state) {

        this.state = state;
    }


    // =========================================
    // PINCODE
    // =========================================

    public String getPincode() {
        return pincode;
    }

    public void setPincode(
            String pincode) {

        this.pincode = pincode;
    }


    // =========================================
    // MILK TYPE
    // =========================================

    public String getMilkType() {
        return milkType;
    }

    public void setMilkType(
            String milkType) {

        this.milkType = milkType;
    }


    // =========================================
    // QUANTITY
    // =========================================

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(
            String quantity) {

        this.quantity = quantity;
    }


    // =========================================
    // DURATION
    // =========================================

    public String getDuration() {
        return duration;
    }

    public void setDuration(
            String duration) {

        this.duration = duration;
    }


    // =========================================
    // DELIVERY TIME
    // =========================================

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(
            String deliveryTime) {

        this.deliveryTime = deliveryTime;
    }


    // =========================================
    // START DATE
    // =========================================

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(
            LocalDate startDate) {

        this.startDate = startDate;
    }


    // =========================================
    // NEXT DELIVERY DATE
    // =========================================

    public LocalDate getNextDeliveryDate() {
        return nextDeliveryDate;
    }

    public void setNextDeliveryDate(
            LocalDate nextDeliveryDate) {

        this.nextDeliveryDate =
                nextDeliveryDate;
    }


    // =========================================
    // STATUS
    // =========================================

    public String getStatus() {
        return status;
    }

    public void setStatus(
            String status) {

        this.status = status;
    }


    // =========================================
    // CREATED AT
    // =========================================

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {

        this.createdAt = createdAt;
    }


    // =========================================
    // PAYMENT STATUS
    // =========================================

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(
            String paymentStatus) {

        this.paymentStatus =
                paymentStatus;
    }


    // =========================================
    // RAZORPAY PLAN ID
    // =========================================

    public String getRazorpayPlanId() {
        return razorpayPlanId;
    }

    public void setRazorpayPlanId(
            String razorpayPlanId) {

        this.razorpayPlanId =
                razorpayPlanId;
    }


    // =========================================
    // RAZORPAY SUBSCRIPTION ID
    // =========================================

    public String getRazorpaySubscriptionId() {
        return razorpaySubscriptionId;
    }

    public void setRazorpaySubscriptionId(
            String razorpaySubscriptionId) {

        this.razorpaySubscriptionId =
                razorpaySubscriptionId;
    }


    // =========================================
    // RAZORPAY PAYMENT ID
    // =========================================

    public String getRazorpayPaymentId() {
        return razorpayPaymentId;
    }

    public void setRazorpayPaymentId(
            String razorpayPaymentId) {

        this.razorpayPaymentId =
                razorpayPaymentId;
    }


    // =========================================
    // RAZORPAY SIGNATURE
    // =========================================

    public String getRazorpaySignature() {
        return razorpaySignature;
    }

    public void setRazorpaySignature(
            String razorpaySignature) {

        this.razorpaySignature =
                razorpaySignature;
    }

}