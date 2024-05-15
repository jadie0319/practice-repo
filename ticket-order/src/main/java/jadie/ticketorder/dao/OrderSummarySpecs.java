package jadie.ticketorder.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class OrderSummarySpecs {

    public static Specification<OrderSummary> orderId(String orderId) {
        return (
                Root<OrderSummary> root,
                CriteriaQuery<?> query,
                CriteriaBuilder cb) -> cb.equal(root.<String>get("orderId"), orderId);
    }

    public static Specification<OrderSummary> orderDateBetween(LocalDateTime from, LocalDateTime to) {
        return (
                Root<OrderSummary> root,
                CriteriaQuery<?> query,
                CriteriaBuilder cb) -> cb.between(root.get("orderDate"), from, to);
    }
}
