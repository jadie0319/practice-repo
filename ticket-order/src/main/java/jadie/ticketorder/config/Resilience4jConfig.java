package jadie.ticketorder.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.Feign;
import feign.Logger;
import feign.Retryer;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4jConfig {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(Resilience4jConfig.class);

    @Bean
    public CircuitBreakerConfig circuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                .failureRateThreshold(50) // 실패율 임계값
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(20)
                .minimumNumberOfCalls(10)
                .permittedNumberOfCallsInHalfOpenState(1) // 반열림 상태에서 호출 허용 개수
                .waitDurationInOpenState(Duration.ofSeconds(10L)) // 열린 상태에서 10초 대기
                .build();
    }

    @Bean
    public CircuitBreaker circuitBreaker(CircuitBreakerRegistry circuitBreakerRegistry) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("circuitBreaker");

        circuitBreaker.getEventPublisher()
                .onCallNotPermitted(event -> {
                    logger.info("CircuitBreaker - Call not permitted at {}", event.getCreationTime());
                })
                .onError(event -> {
                    logger.info("CircuitBreaker - Call failed at {}  with exception: {}", event.getCreationTime(), event.getThrowable().getClass().getName());
                })
                .onSuccess(event -> {
                    logger.info("CircuitBreaker - Call succeeded at {}", event.getCreationTime());
                })
                .onStateTransition(event -> {
                    logger.info("CircuitBreaker state changed from {} to {} at {}",
                            event.getStateTransition().getFromState()
                            , event.getStateTransition().getToState()
                            , event.getCreationTime());
                });

        return circuitBreaker;
    }

    @Bean
    public Retry retry() {
        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(1000)) // 1초 고정 대기
                .retryExceptions(Exception.class) // 모든 예외에 대해 재시도
                .intervalFunction(IntervalFunction.ofExponentialRandomBackoff(
                        Duration.ofMillis(500), // 초기 500ms
                        2.0, // 지수 승수 x2. 초기가 1초였다면, 1초대기,2초대기,4초대기,8초대기 이런식으로 늘어난다.
                        0.3, // 30% 랜덤화 지수 백오프에 랜덤 요소를 추가하여 여러 클라이언트가 동시에 재시도할 때 발생할 수 있는 "재시도 폭풍(retry storm)"을 방지
                        // 4초의 대기가 있을 때 0.5 의 랜덤화 지수를 넣어주면. -2,+2 하여 2~6 범위 내에서 랜덤한 초만큼 대기한다.
                        Duration.ofSeconds(20)
                ))
                .build();
        Retry retry = RetryRegistry.of(retryConfig)
                .retry("retry");

        retry.getEventPublisher()
                .onRetry(event -> logger.info("Retry - Retry event: {}", event))
                .onSuccess(event -> logger.info("Retry - Success event: {}", event))
                .onError(event -> logger.error("Retry - Error event: {}", event));

        return retry;
    }

    @Bean
    public RateLimiterConfig rateLimiterConfig() {
        return RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofMillis(500)) // 버킷의 토큰 초기화 주기
                .limitForPeriod(10) // 토큰의 수, 500ms 당 10개 요청으로 제한, 초당 10개 요청으로 제한된 경우 버킷에는 10개의 토큰이 있고 매초 리필됩니다.(토큰 버킷 알고리즘, 버킷이 비면 새 토큰이 생길 때까지 스레드가 대기합니다.)
                .timeoutDuration(Duration.ofSeconds(1L)) // 버킷에 토큰이 없을 시 대기하는 시간 //TODO  허가를 기다리는 스레드의 대기시간. 허가를 얻지 못하면 RequestNotPermitted 예외 발생 -> 실패 큐에 넣게 해야한다.
                .build();
    }

    @Bean
    public RateLimiter rateLimiter(RateLimiterRegistry rateLimiterRegistry) {
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("rateLimiter");

        rateLimiter.getEventPublisher()
                .onSuccess(event -> {
                    logger.info("RateLimiter - success: {}", event);
                })
                .onFailure(throwable -> {
                    logger.info("RateLimiter - failure: {}", throwable);
                });

        return rateLimiter;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return (new ObjectMapper())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.INDENT_OUTPUT, true)
                .registerModules(new JavaTimeModule(), new Jdk8Module());
    }

}
