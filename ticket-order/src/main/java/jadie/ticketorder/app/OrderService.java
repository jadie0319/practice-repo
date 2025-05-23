package jadie.ticketorder.app;

import jadie.ticketorder.dto.OrderResponse;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class OrderService {


    private final Function<Long, OrderResponse> getOrderPurchaseFunction;

    public OrderService(Function<Long, OrderResponse> getOrderPurchaseFunction) {
        this.getOrderPurchaseFunction = getOrderPurchaseFunction;
    }

    public OrderResponse getOrder(Long orderId) {
        OrderResponse response = getOrderPurchaseFunction.apply(orderId);
        return response;
    }
}
