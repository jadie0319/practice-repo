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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class Resilience4jTestForBlog {
    private Logger logger = LoggerFactory.getLogger(Resilience4jTestForBlog.class);


    @DisplayName("Resilience4j 테스트")
    @Test
    void resilience4jTest() throws InterruptedException {
        CircuitBreaker circuitBreaker = getCircuitBreaker();
        addCircuitBreakerMonitoring(circuitBreaker);

        RateLimiter rateLimiter = getRateLimiter();
        addRateLimiterMonitor(rateLimiter);

        Retry retry = getRetry();
        addRetryMonitor(retry);

        MockPayApiClient mockPayApiClient = new MockPayApiClient();
//        Function<Integer, ResponseEntity<Void>> decoratedFunction = Retry.decorateFunction(
//                retry,
//                RateLimiter.decorateFunction(
//                        rateLimiter,
//                        CircuitBreaker.decorateFunction(
//                                circuitBreaker,
//                                (Integer event) -> mockPayApiClient.pay(event)
//                        )
//                )
//        );

        Function<Integer, ResponseEntity<Void>> decoratedFunction = CircuitBreaker.decorateFunction(
                circuitBreaker,
                RateLimiter.decorateFunction(
                        rateLimiter,
                        Retry.decorateFunction(
                                retry,
                                (Integer event) -> mockPayApiClient.pay(event)
                        )
                )
        );

//        int threadCount = 15;
//        ExecutorService executorService = Executors.newFixedThreadPool(15);
//        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
//
//        System.out.println("테스트 시작");
//
////        for (int i=1 ;i <= threadCount ; i++) {
////            Integer id = i;
////            executorService.submit(() -> {
////                try {
////                    ResponseEntity<Void> response = decoratedFunction.apply(id);
////                } finally {
////                    countDownLatch.countDown();
////                }
////            });
////        }
////        countDownLatch.await();

        // 홀수면 성공
        for (int i = 1; i <= 15; i++) {
            System.out.println();
            System.out.println();

            try {
                // 홀수면 성공, 짝수면 실패한다.
                System.out.println("Request #" + i + ": " + i);
                ResponseEntity<Void> response = decoratedFunction.apply(i);
            } catch (RequestNotPermitted e) {
                System.out.println("RequestNotPermitted Request #" + i + " - RequestNotPermitted: " + e.getMessage());
            } catch (CallNotPermittedException e) {
                System.out.println("CallNotPermittedException Request #" + i + " - CallNotPermittedException: " + e.getMessage());
            } catch (FeignException e) {
                System.out.println("FeignException Request #" + i + " - FeignException Retry: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Exception Request #" + i + " - Exception: " + e.getMessage());
            }
        }

        // 1초 대기 후 다시 시도 (토큰이 리필되었는지 확인)
        Thread.sleep(10000);

        System.out.println("Final CircuitBreaker State: " + circuitBreaker.getState());
        System.out.println("CircuitBreaker Metrics: " +
                "Successful Calls: " + circuitBreaker.getMetrics().getNumberOfSuccessfulCalls() + ", " +
                "Failed Calls: " + circuitBreaker.getMetrics().getNumberOfFailedCalls() + ", " +
                "Not Permitted Calls: " + circuitBreaker.getMetrics().getNumberOfNotPermittedCalls());

        System.out.println("Ratelimiter Metrics: " +
                " AvailablePermissions: " + rateLimiter.getMetrics().getAvailablePermissions() +
                " NumberOfWaitingThreads: " + rateLimiter.getMetrics().getNumberOfWaitingThreads()
        );



        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);
    }

    private void addRetryMonitor(Retry retry) {
        retry.getEventPublisher()
                .onSuccess(e -> logger.info("\nRETRY - Successfully added retry event: " + e))
                .onError(e -> logger.info("\nRETRY - Error: " + e));
//                .onRetry(e -> System.out.println("\nRETRY - Retry event: " + e));
    }

    private void addRateLimiterMonitor(RateLimiter rateLimiter) {
        rateLimiter.getEventPublisher()
                .onSuccess(event -> {
                    logger.info("RL - success: "+ " AvailablePermissions: " + rateLimiter.getMetrics().getAvailablePermissions() + " NumberOfWaitingThreads: " + rateLimiter.getMetrics().getNumberOfWaitingThreads());
                })
                .onFailure(throwable -> {
                    logger.info("RL - failed: " + throwable + " AvailablePermissions: " + rateLimiter.getMetrics().getAvailablePermissions() + " NumberOfWaitingThreads: " + rateLimiter.getMetrics().getNumberOfWaitingThreads());
                })
//                .onEvent(event -> {
//                    logger.info("RL - event : " + event);
//                })
        ;
    }

    private void addCircuitBreakerMonitoring(CircuitBreaker circuitBreaker) {
        circuitBreaker.getEventPublisher()
                .onCallNotPermitted(event -> {
                    logger.info("CB - Call not permitted. failRate: " + circuitBreaker.getMetrics().getFailureRate() +  " notPermittedCall: " + circuitBreaker.getMetrics().getNumberOfNotPermittedCalls() + " successCall: " + circuitBreaker.getMetrics().getNumberOfSuccessfulCalls() + " failedCall: " + circuitBreaker.getMetrics().getNumberOfFailedCalls());
                })
                .onError(event -> {
                    logger.info("CB - Call successCall: " + circuitBreaker.getMetrics().getNumberOfSuccessfulCalls() + " failedCall: " + circuitBreaker.getMetrics().getNumberOfFailedCalls() + " notPermittedCall:" + circuitBreaker.getMetrics().getNumberOfNotPermittedCalls());
                    //System.out.println("CB - Call failed at " + event.getCreationTime() + " with exception: " + event.getThrowable().getClass().getName());
                })
                .onSuccess(event -> {
                    logger.info("CB - Call successCall: " + circuitBreaker.getMetrics().getNumberOfSuccessfulCalls() + " failedCall: " + circuitBreaker.getMetrics().getNumberOfFailedCalls() + " notPermittedCall:" + circuitBreaker.getMetrics().getNumberOfNotPermittedCalls());
                })
                .onStateTransition(event -> {
                    logger.info("CB - CircuitBreaker state changed from " +
                            event.getStateTransition().getFromState() +
                            " to " + event.getStateTransition().getToState() +
                            " at " + event.getCreationTime());
                });
    }

    private CircuitBreaker getCircuitBreaker() {
        return CircuitBreaker.of
                (
                        "payApiClientCircuitBreaker",
                        CircuitBreakerConfig.custom()
                                .failureRateThreshold(50) // 실패율 임계값
                                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                                .slidingWindowSize(20)
                                //.slidingWindowSynchronizationStrategy(CircuitBreakerConfig.SlidingWindowSynchronizationStrategy.LOCK_FREE)
                                .minimumNumberOfCalls(10)
                                .permittedNumberOfCallsInHalfOpenState(5) // 반열림 상태에서 호출 허용 개수
                                .waitDurationInOpenState(Duration.ofSeconds(10L)) // 열린 상태에서 10초 대기
                                //.ignoreExceptions(RetryableException.class)
                                //.recordExceptions(RuntimeException.class)
                                //.ignoreExceptions(RuntimeException.class)
                                .build()
                );
    }

    @NotNull
    private Retry getRetry() {
        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(1000)) // 1초 고정 대기
//                .retryExceptions(IllegalArgumentException.class)
                .retryExceptions(Exception.class) // 모든 예외에 대해 재시도
//                .retryOnException( // FeignClientException, SocketTimeoutExcepion 발생 시 재시도하지 않음
//                        throwable -> !(throwable instanceof FeignException.FeignClientException)
//                                && !(throwable instanceof SocketTimeoutException))
                .intervalFunction(IntervalFunction.ofExponentialRandomBackoff(
                        Duration.ofMillis(500), // 초기 500ms
                        2.0, // 지수 승수 x2
                        0.3, // 30% 랜덤화 지수 백오프에 랜덤 요소를 추가하여 여러 클라이언트가 동시에 재시도할 때 발생할 수 있는 "재시도 폭풍(retry storm)"을 방지
                        Duration.ofSeconds(20)
                ))
                .build();
        return RetryRegistry.of(retryConfig)
                .retry("payApiClientRetry");
    }

    @NotNull
    private RateLimiter getRateLimiter() {
        RateLimiterConfig rateLimiterConfig = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofMillis(5000))
                .limitForPeriod(2) // 초당 10개 요청으로 제한, 초당 10개 요청으로 제한된 경우 버킷에는 10개의 토큰이 있고 매초 리필됩니다.(토큰 버킷 알고리즘, 버킷이 비면 새 토큰이 생길 때까지 스레드가 대기합니다.)
                .timeoutDuration(Duration.ofSeconds(1L)) //  허가를 기다리는 스레드의 대기시간. 허가를 얻지 못하면 RequestNotPermitted 예외 발생
                .build();
        return RateLimiterRegistry.of(rateLimiterConfig)
                .rateLimiter("payApiClientRateLimiter");
    }


}
