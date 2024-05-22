package jadie.ticketorder.dao;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderSummaryService {

    private final OrderSummaryRepository repository;

    public OrderSummaryService(OrderSummaryRepository repository) {
        this.repository = repository;
    }

    public void and() {
        Specification<OrderSummary> spec1 = OrderSummarySpecs.orderId("user1");
        Specification<OrderSummary> spec2 = OrderSummarySpecs.orderDateBetween(
                LocalDateTime.of(2024, 5, 15, 0, 0, 0),
                LocalDateTime.of(2024, 5, 16, 0, 0, 0)
        );
        Specification<OrderSummary> spec3 = spec1.and(spec2);
    }

    public void exam(OrderSummaryRequest request) {
        Specification<OrderSummary> specs = Specification.where(null);
        if (StringUtils.hasText(request.ordererName())) {
            specs = specs.and(OrderSummarySpecs.ordererName(request.ordererName()));
        }
        if (!ObjectUtils.isEmpty(request.from()) && !ObjectUtils.isEmpty(request.to())) {
            specs = specs.and(OrderSummarySpecs.orderDateBetween(request.from(), request.to()));
        }

        List<OrderSummary> orderSummaries = repository.findAll(specs);
    }

    public void exam2(OrderSummaryRequest request) {
        Specification<OrderSummary> specs = SpecBuilder.builder(OrderSummary.class)
                .ifHasText(request.ordererName(), name -> OrderSummarySpecs.ordererName(request.ordererName()))
                .toSpec();
        List<OrderSummary> orderSummaries = repository.findAll(specs);
    }


    public void not() {
        Specification<OrderSummary> user1 = Specification.not(OrderSummarySpecs.orderId("user1"));
    }
}
