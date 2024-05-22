package jadie.ticketorder.dao;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderSummaryRepository extends JpaRepository<OrderSummary, Long> {
    List<OrderSummary> findAll(Specification<OrderSummary> spec);

//    @Query("""
//            select new jadie.ticketorder.dao.OrderSummaryView
//                (
//                    o.name, o.state, m.name, m.id, p.name
//                )
//            from Order o, Member m, Product p
//            where ~~~~
//            """)
//    List<OrderSummaryView> findOrderSummaryView();
}
