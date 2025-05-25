package jadie.ticketorder.config;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomMetricTest {
    private final MeterRegistry meterRegistry;

    @Scheduled(fixedDelay = 1000)
    public void sendTestMetric() {
        meterRegistry.counter("test.custom.metric", "type", "test").increment();
    }
}
