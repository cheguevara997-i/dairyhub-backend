package dairyhub_backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "reviews",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_user_product_review",
                        columnNames = {
                                "user_email",
                                "product_id"
                        }
                )
        }
)
public class Review {

    // =========================================
    // ID
    // =========================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================
    // PRODUCT DETAILS
    // =========================================

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_name")
    private String productName;


    // =========================================
    // CUSTOMER DETAILS
    // =========================================

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "user_name", nullable = false)
    private String userName;


    // =========================================
    // REVIEW DETAILS
    // =========================================

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false, length = 1000)
    private String comment;


    // =========================================
    // VERIFIED PURCHASE
    // =========================================

    @Column(nullable = false)
    private Boolean verifiedPurchase = false;


    // =========================================
    // CREATED DATE
    // =========================================

    @Column(nullable = false)
    private LocalDateTime createdAt;


    // =========================================
    // CONSTRUCTOR
    // =========================================

    public Review() {
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
    // PRODUCT ID
    // =========================================

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }


    // =========================================
    // PRODUCT NAME
    // =========================================

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }


    // =========================================
    // USER EMAIL
    // =========================================

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }


    // =========================================
    // USER NAME
    // =========================================

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    // =========================================
    // RATING
    // =========================================

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }


    // =========================================
    // COMMENT
    // =========================================

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    // =========================================
    // VERIFIED PURCHASE
    // =========================================

    public Boolean getVerifiedPurchase() {
        return verifiedPurchase;
    }

    public void setVerifiedPurchase(Boolean verifiedPurchase) {
        this.verifiedPurchase = verifiedPurchase;
    }


    // =========================================
    // CREATED AT
    // =========================================

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}