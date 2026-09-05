package dairyhub_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import dairyhub_backend.entity.Product;
import dairyhub_backend.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;


    public ProductService(
            ProductRepository productRepository) {

        this.productRepository =
                productRepository;
    }


    // =========================================
    // GET ALL PRODUCTS
    // =========================================

    public List<Product> getAllProducts() {

        return productRepository.findAll();
    }


    // =========================================
    // GET PRODUCT BY ID
    // =========================================

    public Product getProductById(
            Long id) {

        return productRepository.findById(
                id
        )
        .orElseThrow(
                () ->
                        new RuntimeException(
                                "Product not found"
                        )
        );
    }


    // =========================================
    // ADD PRODUCT
    // =========================================

    public Product addProduct(
            Product product) {

        /*
         * New products are available by default.
         *
         * If the frontend doesn't send available,
         * treat it as true.
         */

        if (
                product.getAvailable() == null
        ) {

            product.setAvailable(
                    true
            );
        }


        return productRepository.save(
                product
        );
    }


    // =========================================
    // UPDATE PRODUCT
    // =========================================

    public Product updateProduct(
            Long id,
            Product product) {

        Product existingProduct =
                getProductById(
                        id
                );


        // =====================================
        // NAME
        // =====================================

        existingProduct.setName(
                product.getName()
        );


        // =====================================
        // CATEGORY
        // =====================================

        existingProduct.setCategory(
                product.getCategory()
        );


        // =====================================
        // PRICE
        // =====================================

        existingProduct.setPrice(
                product.getPrice()
        );


        // =====================================
        // SIZE
        // =====================================

        existingProduct.setSize(
                product.getSize()
        );


        // =====================================
        // STOCK
        // =====================================

        existingProduct.setStock(
                product.getStock()
        );


        // =====================================
        // DESCRIPTION
        // =====================================

        existingProduct.setDescription(
                product.getDescription()
        );


        // =====================================
        // IMAGE
        // =====================================

        existingProduct.setImage(
                product.getImage()
        );


        // =====================================
        // AVAILABILITY
        // =====================================

        /*
         * If available is omitted during an old-style
         * update request, keep the existing value.
         *
         * This prevents an accidental reset.
         */

        if (
                product.getAvailable() != null
        ) {

            existingProduct.setAvailable(
                    product.getAvailable()
            );
        }


        return productRepository.save(
                existingProduct
        );
    }


    // =========================================
    // MARK AVAILABLE / UNAVAILABLE
    // =========================================

    public Product setAvailability(
            Long id,
            boolean available) {

        Product product =
                getProductById(
                        id
                );


        product.setAvailable(
                available
        );


        return productRepository.save(
                product
        );
    }


    // =========================================
    // DELETE PRODUCT
    // =========================================

    public void deleteProduct(
            Long id) {

        productRepository.deleteById(
                id
        );
    }

}