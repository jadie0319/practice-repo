package jadie.ticketorder.dao;

import java.util.List;
import java.util.Optional;

public interface OrderDataDao {
    Optional<OrderData> findById(Long orderId);
    List<OrderData> findByOrders(String orderIds);
}
