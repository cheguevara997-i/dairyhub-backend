package dairyhub_backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dairyhub_backend.entity.CustomerOrder;
import dairyhub_backend.entity.OrderItem;
import dairyhub_backend.entity.Product;
import dairyhub_backend.repository.OrderRepository;
import dairyhub_backend.repository.ProductRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;


    // =========================================
    // CONSTRUCTOR
    // =========================================

    public OrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository) {

        this.orderRepository =
                orderRepository;

        this.productRepository =
                productRepository;
    }


    // =========================================
    // CREATE NEW ORDER
    // =========================================

    @Transactional
    public CustomerOrder createOrder(
            CustomerOrder order) {

        if (order == null) {

            throw new RuntimeException(
                    "Order is required."
            );
        }


        if (
                order.getItems() == null ||
                order.getItems().isEmpty()
        ) {

            throw new RuntimeException(
                    "Order must contain at least one product."
            );
        }


        /*
         * Validate every product against the
         * CURRENT database values.
         *
         * We do NOT trust:
         *
         * - frontend price
         * - frontend stock
         * - frontend subtotal
         */

        for (
                OrderItem item :
                order.getItems()
        ) {

            if (
                    item == null
            ) {

                throw new RuntimeException(
                        "Invalid order item."
                );
            }


            // =====================================
            // PRODUCT ID
            // =====================================

            if (
                    item.getProductId() == null
            ) {

                throw new RuntimeException(
                        "Product ID is required for every order item."
                );
            }


            // =====================================
            // QUANTITY
            // =====================================

            if (
                    item.getQuantity() == null ||
                    item.getQuantity() <= 0
            ) {

                throw new RuntimeException(
                        "Order quantity must be greater than zero."
                );
            }


            // =====================================
            // FIND CURRENT PRODUCT
            // =====================================

            Product product =
                    productRepository
                            .findById(
                                    item.getProductId()
                            )
                            .orElse(null);


            if (
                    product == null
            ) {

                throw new RuntimeException(
                        "Product with ID " +
                        item.getProductId() +
                        " was not found."
                );
            }


            // =====================================
            // CHECK AVAILABILITY
            // =====================================

            if (
                    !Boolean.TRUE.equals(
                            product.getAvailable()
                    )
            ) {

                throw new RuntimeException(
                        product.getName() +
                        (
                                product.getSize() != null
                                        ? " (" +
                                          product.getSize() +
                                          ")"
                                        : ""
                        ) +
                        " is currently out of stock."
                );
            }


            // =====================================
            // CHECK CURRENT STOCK
            // =====================================

            Integer currentStock =
                    product.getStock();


            if (
                    currentStock == null ||
                    currentStock <= 0
            ) {

                throw new RuntimeException(
                        product.getName() +
                        " is currently out of stock."
                );
            }


            if (
                    item.getQuantity() >
                    currentStock
            ) {

                throw new RuntimeException(
                        "Only " +
                        currentStock +
                        " unit(s) of " +
                        product.getName() +
                        " are available."
                );
            }


            // =====================================
            // USE DATABASE PRICE
            // =====================================

            Double currentPrice =
                    product.getPrice();


            if (
                    currentPrice == null
            ) {

                throw new RuntimeException(
                        "Product price is not available."
                );
            }


            item.setProductName(
                    product.getName()
            );


            item.setPrice(
                    currentPrice
            );


            item.setSize(
                    product.getSize()
            );


            // =====================================
            // CALCULATE SUBTOTAL
            // =====================================

            double subtotal =
                    currentPrice *
                    item.getQuantity();


            item.setSubtotal(
                    subtotal
            );

        }


        // =========================================
        // CALCULATE TOTAL
        // =========================================

        double totalAmount =
                0.0;


        for (
                OrderItem item :
                order.getItems()
        ) {

            totalAmount +=
                    item.getSubtotal();

        }


        order.setTotalAmount(
                totalAmount
        );


        // =========================================
        // SET ORDER RELATIONSHIP
        // =========================================

        for (
                OrderItem item :
                order.getItems()
        ) {

            item.setOrder(
                    order
            );

        }


        // =========================================
        // SAVE ORDER
        // =========================================

        CustomerOrder savedOrder =
                orderRepository.save(
                        order
                );


        // =========================================
        // REDUCE STOCK
        // =========================================

        for (
                OrderItem item :
                savedOrder.getItems()
        ) {

            Product product =
                    productRepository
                            .findById(
                                    item.getProductId()
                            )
                            .orElseThrow(
                                    () ->
                                            new RuntimeException(
                                                    "Product no longer exists."
                                            )
                            );


            int remainingStock =
                    product.getStock() -
                    item.getQuantity();


            product.setStock(
                    Math.max(
                            0,
                            remainingStock
                    )
            );


            // =====================================
            // AUTOMATIC OUT OF STOCK
            // =====================================

            if (
                    remainingStock <= 0
            ) {

                product.setStock(
                        0
                );

                product.setAvailable(
                        false
                );
            }


            productRepository.save(
                    product
            );

        }


        return savedOrder;
    }


    // =========================================
    // GET ALL ORDERS
    // =========================================

    public List<CustomerOrder> getAllOrders() {

        return orderRepository.findAll();
    }


    // =========================================
    // GET ORDERS BY CUSTOMER EMAIL
    // =========================================

    public List<CustomerOrder>
    getOrdersByCustomerEmail(
            String customerEmail) {

        return orderRepository
                .findByCustomerEmail(
                        customerEmail
                );
    }


    // =========================================
    // GET ORDER BY ID
    // =========================================

    public CustomerOrder getOrderById(
            Long id) {

        return orderRepository
                .findById(id)
                .orElse(null);
    }


    // =========================================
    // UPDATE ORDER
    // =========================================

    public CustomerOrder updateOrder(
            Long id,
            CustomerOrder updatedOrder) {

        CustomerOrder existingOrder =
                orderRepository
                        .findById(id)
                        .orElse(null);


        if (
                existingOrder == null
        ) {

            return null;
        }


        existingOrder.setCustomerName(
                updatedOrder.getCustomerName()
        );


        existingOrder.setCustomerEmail(
                updatedOrder.getCustomerEmail()
        );


        existingOrder.setPhone(
                updatedOrder.getPhone()
        );


        existingOrder.setAddress(
                updatedOrder.getAddress()
        );


        existingOrder.setCity(
                updatedOrder.getCity()
        );


        existingOrder.setState(
                updatedOrder.getState()
        );


        existingOrder.setPincode(
                updatedOrder.getPincode()
        );


        /*
         * Preserve the existing order-management
         * behavior.
         */

        existingOrder.setTotalAmount(
                updatedOrder.getTotalAmount()
        );


        existingOrder.setStatus(
                updatedOrder.getStatus()
        );


        return orderRepository.save(
                existingOrder
        );
    }


    // =========================================
    // CHECK ORDER EXPERIENCE FEEDBACK
    // ELIGIBILITY
    // =========================================

    public boolean canSubmitExperienceFeedback(
            Long orderId,
            String customerEmail) {

        // ---------------------------------------
        // BASIC VALIDATION
        // ---------------------------------------

        if (
                orderId == null ||
                customerEmail == null ||
                customerEmail.trim().isEmpty()
        ) {

            return false;
        }


        // ---------------------------------------
        // FIND ORDER
        // ---------------------------------------

        CustomerOrder order =
                getOrderById(
                        orderId
                );


        if (
                order == null
        ) {

            return false;
        }


        // ---------------------------------------
        // OWNERSHIP CHECK
        // ---------------------------------------

        if (
                order.getCustomerEmail() == null
        ) {

            return false;
        }


        if (
                !order.getCustomerEmail()
                        .trim()
                        .equalsIgnoreCase(
                                customerEmail.trim()
                        )
        ) {

            return false;
        }


        // ---------------------------------------
        // STATUS CHECK
        // ---------------------------------------

        String status =
                order.getStatus() == null
                        ? ""
                        : order.getStatus()
                                .trim()
                                .toUpperCase();


        /*
         * Feedback is allowed only for:
         *
         * DELIVERED
         * CANCELLED
         */

        if (
                !"DELIVERED".equals(
                        status
                )
                &&
                !"CANCELLED".equals(
                        status
                )
        ) {

            return false;
        }


        // ---------------------------------------
        // DUPLICATE CHECK
        // ---------------------------------------

        if (
                order.getExperienceRating() != null
        ) {

            return false;
        }


        return true;
    }


    // =========================================
    // SUBMIT ORDER EXPERIENCE FEEDBACK
    // =========================================

    public CustomerOrder submitExperienceFeedback(
            Long orderId,
            String customerEmail,
            Integer rating,
            String feedback) {

        // ---------------------------------------
        // ORDER ID
        // ---------------------------------------

        if (
                orderId == null
        ) {

            throw new RuntimeException(
                    "Order ID is required."
            );
        }


        // ---------------------------------------
        // CUSTOMER EMAIL
        // ---------------------------------------

        if (
                customerEmail == null ||
                customerEmail.trim().isEmpty()
        ) {

            throw new RuntimeException(
                    "Customer email is required."
            );
        }


        // ---------------------------------------
        // RATING
        // ---------------------------------------

        if (
                rating == null ||
                rating < 1 ||
                rating > 5
        ) {

            throw new RuntimeException(
                    "Rating must be between 1 and 5."
            );
        }


        // ---------------------------------------
        // FEEDBACK
        // ---------------------------------------

        if (
                feedback == null ||
                feedback.trim().isEmpty()
        ) {

            throw new RuntimeException(
                    "Feedback is required."
            );
        }


        String cleanedFeedback =
                feedback.trim();


        if (
                cleanedFeedback.length() > 1000
        ) {

            throw new RuntimeException(
                    "Feedback cannot exceed 1000 characters."
            );
        }


        // ---------------------------------------
        // FIND ORDER
        // ---------------------------------------

        CustomerOrder order =
                getOrderById(
                        orderId
                );


        if (
                order == null
        ) {

            throw new RuntimeException(
                    "Order not found."
            );
        }


        // ---------------------------------------
        // OWNERSHIP CHECK
        // ---------------------------------------

        if (
                order.getCustomerEmail() == null ||
                !order.getCustomerEmail()
                        .trim()
                        .equalsIgnoreCase(
                                customerEmail.trim()
                        )
        ) {

            throw new RuntimeException(
                    "You can review only your own order."
            );
        }


        // ---------------------------------------
        // STATUS CHECK
        // ---------------------------------------

        String status =
                order.getStatus() == null
                        ? ""
                        : order.getStatus()
                                .trim()
                                .toUpperCase();


        if (
                !"DELIVERED".equals(
                        status
                )
                &&
                !"CANCELLED".equals(
                        status
                )
        ) {

            throw new RuntimeException(
                    "Order experience feedback is available only after delivery or cancellation."
            );
        }


        // ---------------------------------------
        // DUPLICATE CHECK
        // ---------------------------------------

        if (
                order.getExperienceRating() != null
        ) {

            throw new RuntimeException(
                    "You have already submitted feedback for this order."
            );
        }


        // ---------------------------------------
        // SAVE FEEDBACK
        // ---------------------------------------

        order.setExperienceRating(
                rating
        );


        order.setExperienceFeedback(
                cleanedFeedback
        );


        order.setExperienceFeedbackAt(
                LocalDateTime.now()
        );


        return orderRepository.save(
                order
        );
    }


    // =========================================
    // DELETE ORDER
    // =========================================

    public boolean deleteOrder(
            Long id) {

        if (
                !orderRepository.existsById(
                        id
                )
        ) {

            return false;
        }


        orderRepository.deleteById(
                id
        );


        return true;
    }

}