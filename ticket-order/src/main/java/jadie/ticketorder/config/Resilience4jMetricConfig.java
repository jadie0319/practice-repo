package jadie.ticketorder.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.micrometer.tagged.TaggedCircuitBreakerMetrics;
import io.github.resilience4j.micrometer.tagged.TaggedRateLimiterMetrics;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Resilience4jMetricConfig {

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry(MeterRegistry meterRegistry, CircuitBreakerConfig circuitBreakerConfig) {
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(circuitBreakerConfig);
        // Micrometer 태그 바인딩
        TaggedCircuitBreakerMetrics.ofCircuitBreakerRegistry(registry)
                .bindTo(meterRegistry);
        return registry;
    }

    @Bean
    public RateLimiterRegistry rateLimiterRegistry(MeterRegistry meterRegistry, RateLimiterConfig rateLimiterConfig) {
        RateLimiterRegistry registry = RateLimiterRegistry.of(rateLimiterConfig);
        // Micrometer 태그 바인딩
        TaggedRateLimiterMetrics.ofRateLimiterRegistry(registry)
                .bindTo(meterRegistry);
        return registry;
    }
}
