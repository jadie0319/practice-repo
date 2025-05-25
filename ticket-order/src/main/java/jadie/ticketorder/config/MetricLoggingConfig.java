package jadie.ticketorder.config;

import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnStateTransitionEvent;
import io.micrometer.statsd.StatsdMeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
@Slf4j
public class MetricLoggingConfig {

    @Bean
    public MeterRegistryCustomizer<StatsdMeterRegistry> metricsLogger() {
        return registry -> {
            registry.config().onMeterAdded(meter -> {
                log.info("Meter added: {} - {}", meter.getId().getName(), meter.getId().getTags());
            });
        };
    }

    @EventListener
    public void handleCircuitBreakerEvent(CircuitBreakerOnStateTransitionEvent event) {
        log.info("CircuitBreaker state transition: {} -> {}",
                event.getStateTransition().getFromState(),
                event.getStateTransition().getToState());
    }


}
