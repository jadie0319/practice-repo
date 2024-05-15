package jadie.ticketorder.dao;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderSummaryService {

    public void and() {
        Specification<OrderSummary> spec1 = OrderSummarySpecs.orderId("user1");
        Specification<OrderSummary> spec2 = OrderSummarySpecs.orderDateBetween(
                LocalDateTime.of(2024, 5, 15, 0, 0, 0),
                LocalDateTime.of(2024, 5, 16, 0, 0, 0)
        );
        Specification<OrderSummary> spec3 = spec1.and(spec2);
    }

    public void not() {
        Specification<OrderSummary> user1 = Specification.not(OrderSummarySpecs.orderId("user1"));
    }
}
