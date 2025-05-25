package jadie.ticketorder.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.retry.Retry;
import jadie.ticketorder.app.OrderPurchaseService;
import jadie.ticketorder.dto.OrderResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class OrderServiceResilienceConfig {
    private final CircuitBreaker circuitBreaker;
    private final Retry retry;
    private final RateLimiter rateLimiter;
    private final OrderPurchaseService orderPurchaseService;

    public OrderServiceResilienceConfig(
            CircuitBreaker examSvcApiClientCircuitBreaker, Retry examSvcApiClientRetry,
            RateLimiter examSvcApiClientRateLimiter, OrderPurchaseService orderPurchaseService) {
        this.circuitBreaker = examSvcApiClientCircuitBreaker;
        this.retry = examSvcApiClientRetry;
        this.rateLimiter = examSvcApiClientRateLimiter;
        this.orderPurchaseService = orderPurchaseService;
    }

    @Bean
    public Function<Long, OrderResponse> getOrderPurchaseFunction() {
        return decorateFunction(this::getOrder);
    }

    private OrderResponse getOrder(Long input) {
        return orderPurchaseService.getOrder(input);
    }

    private <T> Function<Long, T> decorateFunction(Function<Long, T> function) {
        return CircuitBreaker.decorateFunction(
                circuitBreaker,
                RateLimiter.decorateFunction(
                        rateLimiter,
                        Retry.decorateFunction(
                                retry,
                                function
                        )
                )
        );
    }
}
