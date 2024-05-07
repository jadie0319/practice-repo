package jadie.ticketorder.domain;

public interface OrderRepository {
    Order findByNumber(OrderNumber orderNumber);
    void save(Order order);
    void delete(Order order);
}
