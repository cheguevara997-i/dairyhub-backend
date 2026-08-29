package dairyhub_backend.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class CustomerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================
    // CUSTOMER DETAILS
    // =========================================

    private String customerName;

    private String customerEmail;

    private String phone;


    // =========================================
    // DELIVERY DETAILS
    // =========================================

    private String address;

    private String city;

    private String state;

    private String pincode;


    // =========================================
    // ORDER DETAILS
    // =========================================

    private Double totalAmount;

    private String status;

    private LocalDateTime orderDate;


    // =========================================
    // PAYMENT DETAILS
    // =========================================

    private String paymentStatus;

    private String razorpayOrderId;

    private String razorpayPaymentId;

    private String razorpaySignature;


    // =========================================
    // ORDER ITEMS
    // =========================================

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItem> items =
            new ArrayList<>();


    // =========================================
    // CONSTRUCTOR
    // =========================================

    public CustomerOrder() {
    }


    // =========================================
    // CREATE ORDER DATE
    // =========================================

    @PrePersist
    public void createOrderDate() {

        orderDate = LocalDateTime.now();

        if (status == null) {
            status = "ORDER_PLACED";
        }

        if (paymentStatus == null) {
            paymentStatus = "PENDING";
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

    public void setPhone(String phone) {
        this.phone = phone;
    }


    // =========================================
    // ADDRESS
    // =========================================

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    // =========================================
    // CITY
    // =========================================

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    // =========================================
    // STATE
    // =========================================

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


    // =========================================
    // PINCODE
    // =========================================

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }


    // =========================================
    // TOTAL AMOUNT
    // =========================================

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(
            Double totalAmount) {

        this.totalAmount = totalAmount;
    }


    // =========================================
    // ORDER STATUS
    // =========================================

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    // =========================================
    // ORDER DATE
    // =========================================

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(
            LocalDateTime orderDate) {

        this.orderDate = orderDate;
    }


    // =========================================
    // PAYMENT STATUS
    // =========================================

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(
            String paymentStatus) {

        this.paymentStatus = paymentStatus;
    }


    // =========================================
    // RAZORPAY ORDER ID
    // =========================================

    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }

    public void setRazorpayOrderId(
            String razorpayOrderId) {

        this.razorpayOrderId =
                razorpayOrderId;
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


    // =========================================
    // ORDER ITEMS
    // =========================================

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(
            List<OrderItem> items) {

        this.items = items;
    }


    // =========================================
    // ADD ORDER ITEM
    // =========================================

    public void addItem(OrderItem item) {

        items.add(item);

        item.setOrder(this);
    }


    // =========================================
    // REMOVE ORDER ITEM
    // =========================================

    public void removeItem(OrderItem item) {

        items.remove(item);

        item.setOrder(null);
    }

}