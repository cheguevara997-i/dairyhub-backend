package dairyhub_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================
    // ORDER RELATIONSHIP
    // =========================================

    /*
     * CustomerOrder contains the list of OrderItem.
     *
     * OrderItem also contains a reference back to
     * CustomerOrder.
     *
     * @JsonIgnore is required here so Jackson does
     * not serialize the order again and create:
     *
     * Order
     *   -> items
     *      -> order
     *         -> items
     *            -> order
     *               -> ...
     */

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private CustomerOrder order;


    // =========================================
    // PRODUCT DETAILS
    // =========================================

    private Long productId;

    private String productName;


    // =========================================
    // PRODUCT SIZE
    // =========================================

    private String size;


    // =========================================
    // QUANTITY
    // =========================================

    private Integer quantity;


    // =========================================
    // PRICE
    // =========================================

    private Double price;


    // =========================================
    // SUBTOTAL
    // =========================================

    private Double subtotal;


    // =========================================
    // CONSTRUCTOR
    // =========================================

    public OrderItem() {
    }


    // =========================================
    // ID
    // =========================================

    public Long getId() {

        return id;
    }


    public void setId(
            Long id) {

        this.id =
                id;
    }


    // =========================================
    // ORDER
    // =========================================

    public CustomerOrder getOrder() {

        return order;
    }


    public void setOrder(
            CustomerOrder order) {

        this.order =
                order;
    }


    // =========================================
    // PRODUCT ID
    // =========================================

    public Long getProductId() {

        return productId;
    }


    public void setProductId(
            Long productId) {

        this.productId =
                productId;
    }


    // =========================================
    // PRODUCT NAME
    // =========================================

    public String getProductName() {

        return productName;
    }


    public void setProductName(
            String productName) {

        this.productName =
                productName;
    }


    // =========================================
    // PRODUCT SIZE
    // =========================================

    public String getSize() {

        return size;
    }


    public void setSize(
            String size) {

        this.size =
                size;
    }


    // =========================================
    // QUANTITY
    // =========================================

    public Integer getQuantity() {

        return quantity;
    }


    public void setQuantity(
            Integer quantity) {

        this.quantity =
                quantity;
    }


    // =========================================
    // PRICE
    // =========================================

    public Double getPrice() {

        return price;
    }


    public void setPrice(
            Double price) {

        this.price =
                price;
    }


    // =========================================
    // SUBTOTAL
    // =========================================

    public Double getSubtotal() {

        return subtotal;
    }


    public void setSubtotal(
            Double subtotal) {

        this.subtotal =
                subtotal;
    }

}