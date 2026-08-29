package dairyhub_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import dairyhub_backend.entity.CustomerOrder;
import dairyhub_backend.entity.OrderItem;
import dairyhub_backend.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    // =========================================
    // CREATE NEW ORDER
    // =========================================

    public CustomerOrder createOrder(
            CustomerOrder order) {

        /*
         * Calculate subtotal for every
         * product in the order.
         */

        if (order.getItems() != null) {

            for (OrderItem item : order.getItems()) {

                if (item.getPrice() != null &&
                    item.getQuantity() != null) {

                    double subtotal =
                            item.getPrice()
                            * item.getQuantity();

                    item.setSubtotal(subtotal);
                }
            }
        }

        return orderRepository.save(order);
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

    public List<CustomerOrder> getOrdersByCustomerEmail(
            String customerEmail) {

        return orderRepository
                .findByCustomerEmail(customerEmail);
    }


    // =========================================
    // GET ORDER BY ID
    // =========================================

    public CustomerOrder getOrderById(Long id) {

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

        if (existingOrder == null) {
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

        existingOrder.setTotalAmount(
                updatedOrder.getTotalAmount()
        );

        existingOrder.setStatus(
                updatedOrder.getStatus()
        );


        return orderRepository.save(existingOrder);
    }


    // =========================================
    // DELETE ORDER
    // =========================================

    public boolean deleteOrder(Long id) {

        if (!orderRepository.existsById(id)) {
            return false;
        }

        orderRepository.deleteById(id);

        return true;
    }

}