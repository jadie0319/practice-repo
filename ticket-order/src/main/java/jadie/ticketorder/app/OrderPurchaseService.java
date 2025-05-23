package jadie.ticketorder.app;

import jadie.ticketorder.dto.OrderResponse;
import org.springframework.stereotype.Service;

@Service
public class OrderPurchaseService {

    public OrderResponse getOrder(Long orderId) {
        if (orderId == null || orderId % 2 != 0) {
            OrderResponse response = new OrderResponse(orderId);
            return response;
        } else {
            throw new IllegalArgumentException("OrderId must be odd number");
        }
    }
}
