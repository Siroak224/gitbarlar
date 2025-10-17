package com.SpringBoot.Controller;

import com.SpringBoot.dto.CreateOrderRequest;
import com.SpringBoot.entity.Order;
import com.SpringBoot.entity.OrderStatus;
import com.SpringBoot.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // Create order (authenticated user)
    @PostMapping("/orders")
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest req, Authentication auth) {
        String username = (auth != null) ? auth.getName() : "guest";
        Order saved = orderService.createOrder(username, req);
        return ResponseEntity.ok(saved);
    }

    // Get single order (owner or admin check omitted for brevity)
    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        Order o = orderService.getOrder(id);
        if (o == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(o);
    }

    // List orders for user
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> listUserOrders(Authentication auth) {
        String username = (auth != null) ? auth.getName() : null;
        if (username == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(orderService.listOrdersForUser(username));
    }

    // Admin: list all orders
    @GetMapping("/admin/orders")
    public ResponseEntity<List<Order>> listAllOrders() {
        return ResponseEntity.ok(orderService.listAllOrders());
    }

    // Update order status (admin)
    @PutMapping("/admin/orders/{id}/status")
    public ResponseEntity<Order> updateStatus(
            @PathVariable Long id, 
            @RequestParam("status") OrderStatus status,
            @RequestParam(value = "reason", required = false) String reason
    ) {
        Order o = orderService.updateStatus(id, status, reason);
        if (o == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(o);
    }
 // User cancels own order
    @PutMapping("/orders/{id}/cancel")
    public ResponseEntity<Order> cancelOrder(
            @PathVariable Long id,
            @RequestParam("reason") String reason,
            Authentication auth
    ) {
        String username = (auth != null) ? auth.getName() : null;
        if (username == null) return ResponseEntity.status(401).build();

        Order order = orderService.getOrder(id);
        if (order == null) return ResponseEntity.notFound().build();

        // Only allow cancel if pending and owned by this user
        if (!order.getUsername().equals(username)) {
            return ResponseEntity.status(403).build();
        }
        if (order.getStatus() != OrderStatus.PENDING) {
            return ResponseEntity.status(400).body(order);
        }

        Order cancelled = orderService.updateStatus(id, OrderStatus.CANCELLED, reason);
        return ResponseEntity.ok(cancelled);
    }

}
