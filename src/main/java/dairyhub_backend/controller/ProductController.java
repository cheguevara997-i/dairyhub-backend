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

import dairyhub_backend.entity.Product;
import dairyhub_backend.service.ProductService;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://dairyhub-five.vercel.app"
})
public class ProductController {

    private final ProductService productService;


    public ProductController(
            ProductService productService) {

        this.productService =
                productService;
    }


    // =========================================
    // GET ALL PRODUCTS
    // =========================================

    @GetMapping
    public List<Product> getAllProducts() {

        return productService.getAllProducts();
    }


    // =========================================
    // GET PRODUCT BY ID
    // =========================================

    @GetMapping("/{id}")
    public Product getProductById(
            @PathVariable Long id) {

        return productService.getProductById(
                id
        );
    }


    // =========================================
    // ADD PRODUCT
    // =========================================

    @PostMapping
    public Product addProduct(
            @RequestBody Product product) {

        return productService.addProduct(
                product
        );
    }


    // =========================================
    // UPDATE PRODUCT
    // =========================================

    @PutMapping("/{id}")
    public Product updateProduct(
            @PathVariable Long id,
            @RequestBody Product product) {

        return productService.updateProduct(
                id,
                product
        );
    }


    // =========================================
    // MARK AVAILABLE / OUT OF STOCK
    // =========================================

    @PutMapping("/{id}/availability")
    public ResponseEntity<Product> setAvailability(
            @PathVariable Long id,
            @RequestBody AvailabilityRequest request) {

        Product updatedProduct =
                productService.setAvailability(
                        id,
                        Boolean.TRUE.equals(
                                request.available()
                        )
                );


        return ResponseEntity.ok(
                updatedProduct
        );
    }


    // =========================================
    // DELETE PRODUCT
    // =========================================

    @DeleteMapping("/{id}")
    public String deleteProduct(
            @PathVariable Long id) {

        productService.deleteProduct(
                id
        );


        return "Product deleted successfully";
    }


    // =========================================
    // AVAILABILITY REQUEST
    // =========================================

    public record AvailabilityRequest(
            Boolean available
    ) {
    }

}