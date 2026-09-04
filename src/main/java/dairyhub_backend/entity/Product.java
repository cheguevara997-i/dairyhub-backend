package dairyhub_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================
    // PRODUCT NAME
    // =========================================

    private String name;


    // =========================================
    // CATEGORY
    // =========================================

    private String category;


    // =========================================
    // PRICE
    // =========================================

    private Double price;


    // =========================================
    // PRODUCT SIZE / QUANTITY
    // =========================================

    /*
     * Examples:
     *
     * 1 L
     * 500 ml
     * 250 ml
     * 1 kg
     * 500 g
     * 200 g
     *
     * This represents the quantity contained
     * in one product unit.
     */

    @Column(name = "size")
    private String size;


    // =========================================
    // IMAGE
    // =========================================

    private String image;


    // =========================================
    // DESCRIPTION
    // =========================================

    private String description;


    // =========================================
    // AVAILABLE STOCK
    // =========================================

    private Integer stock;


    // =========================================
    // EMPTY CONSTRUCTOR
    // =========================================

    public Product() {
    }


    // =========================================
    // CONSTRUCTOR
    // =========================================

    public Product(
            String name,
            String category,
            Double price,
            String size,
            String image,
            String description,
            Integer stock) {

        this.name =
                name;

        this.category =
                category;

        this.price =
                price;

        this.size =
                size;

        this.image =
                image;

        this.description =
                description;

        this.stock =
                stock;
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
    // NAME
    // =========================================

    public String getName() {

        return name;
    }


    public void setName(
            String name) {

        this.name =
                name;
    }


    // =========================================
    // CATEGORY
    // =========================================

    public String getCategory() {

        return category;
    }


    public void setCategory(
            String category) {

        this.category =
                category;
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
    // SIZE
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
    // IMAGE
    // =========================================

    public String getImage() {

        return image;
    }


    public void setImage(
            String image) {

        this.image =
                image;
    }


    // =========================================
    // DESCRIPTION
    // =========================================

    public String getDescription() {

        return description;
    }


    public void setDescription(
            String description) {

        this.description =
                description;
    }


    // =========================================
    // STOCK
    // =========================================

    public Integer getStock() {

        return stock;
    }


    public void setStock(
            Integer stock) {

        this.stock =
                stock;
    }
}