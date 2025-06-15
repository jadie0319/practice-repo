package jadie.ticketorder.config;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class RateLimiterTestForBlog {

    @DisplayName("RateLimiter 테스트")
    @Test
    void rateLimiterTest() throws InterruptedException {
        RateLimiter rateLimiter = getRateLimiter();
        addRateLimiterMonitor(rateLimiter);

        MockPayApiClient mockPayApiClient = new MockPayApiClient();
        Function<Integer, ResponseEntity<Void>> decoratedFunction = RateLimiter.decorateFunction(
                rateLimiter,
                (Integer event) -> mockPayApiClient.pay(event)
        );

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        System.out.println("테스트 시작");

        for (int i=1 ;i <= threadCount ; i++) {
            Integer id = i;
            executorService.submit(() -> {
                try {
                    ResponseEntity<Void> response = decoratedFunction.apply(id);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        // 1초 대기 후 다시 시도 (토큰이 리필되었는지 확인)
        Thread.sleep(10000);


        System.out.println("Ratelimiter Metrics: " +
                " AvailablePermissions: " + rateLimiter.getMetrics().getAvailablePermissions() +
                " NumberOfWaitingThreads: " + rateLimiter.getMetrics().getNumberOfWaitingThreads()
        );

    }

    private void addRateLimiterMonitor(RateLimiter rateLimiter) {
        rateLimiter.getEventPublisher()
                .onSuccess(event -> {
                    System.out.println("RL - success: " + " AvailablePermissions: " + rateLimiter.getMetrics().getAvailablePermissions() + " NumberOfWaitingThreads: " + rateLimiter.getMetrics().getNumberOfWaitingThreads());
                })
                .onFailure(throwable -> {
                    System.out.println("RL - failed: " + throwable + " AvailablePermissions: " + rateLimiter.getMetrics().getAvailablePermissions() + " NumberOfWaitingThreads: " + rateLimiter.getMetrics().getNumberOfWaitingThreads());
                });
    }

    @NotNull
    private RateLimiter getRateLimiter() {
        RateLimiterConfig rateLimiterConfig = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .limitForPeriod(10) // 초당 10개 요청으로 제한, 초당 10개 요청으로 제한된 경우 버킷에는 10개의 토큰이 있고 매초 리필됩니다.(토큰 버킷 알고리즘, 버킷이 비면 새 토큰이 생길 때까지 스레드가 대기합니다.)
                .timeoutDuration(Duration.ofSeconds(5)) //  허가를 기다리는 스레드의 대기시간. 허가를 얻지 못하면 RequestNotPermitted 예외 발생
                .build();
        return RateLimiterRegistry.of(rateLimiterConfig)
                .rateLimiter("payApiClientRateLimiter");
    }


}
