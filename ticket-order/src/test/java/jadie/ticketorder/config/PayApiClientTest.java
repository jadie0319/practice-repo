package jadie.ticketorder.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.*;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.feign.FeignDecorators;
import io.github.resilience4j.feign.Resilience4jFeign;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.net.SocketTimeoutException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
class PayApiClientTest {


    @Test
    void testRateLimiter() throws InterruptedException {
        CircuitBreaker circuitBreaker = getCircuitBreaker();
        addCircuitBreakerMonitoring(circuitBreaker);

        RateLimiter rateLimiter = getRateLimiter();

        Retry retry = getRetry();

        ObjectMapper mapper = getObjectMapper();

        FeignDecorators decorators = FeignDecorators.builder()
                .withCircuitBreaker(circuitBreaker) // 마지막으로 적용
                .withRateLimiter(rateLimiter)
                .withRetry(retry) // 첫번째로 적용
                .build();

        PayApiClient payApiClient = Feign.builder()
                .encoder(new JacksonEncoder(mapper))
                .decoder(new JacksonDecoder(mapper))
                .logger(new Slf4jLogger(PayApiClient.class))
                .retryer(Retryer.NEVER_RETRY)
                .logLevel(Logger.Level.FULL)
                .invocationHandlerFactory(new Resilience4jFeign.InvocationHandlerFactory(decorators))
                .target(PayApiClient.class, "http://localhost:8081");

        // 비동기 처리 예제
        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<CompletableFuture<ResponseEntity<Void>>> futures = new ArrayList<>();

        // i가 짝수일땐 200 응답, i 가 홀수일땐 실패
        for (int i = 1; i < 6; i++) {
            try {
                ResponseEntity<Void> response = payApiClient.pay(i);
                //System.out.println("response: " + response.getBody());
            } catch (RequestNotPermitted e) {
                System.out.println("Request #" + i + " - RequestNotPermitted: " + e.getMessage());
            } catch (CallNotPermittedException e) {
                System.out.println("Request #" + i + " - CallNotPermittedException: " + e.getMessage());
                //e.printStackTrace(); // 스택 트레이스 출력
            } catch (FeignException e) {
                System.out.println("Request #" + i + " - FeignException Retry: " + e.getMessage());
                //e.printStackTrace(); // 스택 트레이스 출력
            }

            // 각 요청 사이에 짧은 지연
            Thread.sleep(100);
        }


        // 1초 대기 후 다시 시도 (토큰이 리필되었는지 확인)
        Thread.sleep(10000);

        System.out.println("Final CircuitBreaker State: " + circuitBreaker.getState());
        System.out.println("CircuitBreaker Metrics: " +
                "Successful Calls: " + circuitBreaker.getMetrics().getNumberOfSuccessfulCalls() + ", " +
                "Failed Calls: " + circuitBreaker.getMetrics().getNumberOfFailedCalls() + ", " +
                "Not Permitted Calls: " + circuitBreaker.getMetrics().getNumberOfNotPermittedCalls()
        );

//        for (int i = 0; i < 10 ; i++) {
//            final int index = i;
//            CompletableFuture<ResponseEntity<Void>> future = CompletableFuture.supplyAsync( () -> {
//
//                try {
//                    ResponseEntity<Void> response = payApiClient.pay(index);
//                    return response;
////                    ResponseEntity<Void> response = examSvcApiClientCreateFile.apply(req);
////                    return response;
//                    //ResponseEntity<Void> response = examSvcApiClient1.createExamFile(String.valueOf(i)); // 홀수면 성공. 짝수면 실패
//                    //System.out.println("Success #" + i + " - Status: " + response.getStatusCode().value());
//                } catch (RequestNotPermitted e) {
//                    System.out.println("Request #" + index + " - RequestNotPermitted: " + e.getMessage());
//                } catch (CallNotPermittedException e) {
//                    System.out.println("Request #" + index + " - CallNotPermittedException: " + e.getMessage());
//                    //e.printStackTrace(); // 스택 트레이스 출력
//                } catch (FeignException e) {
//                    System.out.println("Request #" + index + " - FeignException Retry: " + e.getMessage());
//                    //e.printStackTrace(); // 스택 트레이스 출력
//                }
//                return null;
//
//            }, executor);
//            futures.add(future);
//
//            // 각 요청 사이에 짧은 지연
//            Thread.sleep(100);
//        }
    }

    private void addCircuitBreakerMonitoring(CircuitBreaker circuitBreaker) {
        circuitBreaker.getEventPublisher()
                .onCallNotPermitted(event -> {
                    System.out.println("Call not permitted at " + event.getCreationTime());
                })
                .onError(event -> {
                    System.out.println("Call failed at " + event.getCreationTime() +
                            " with exception: " + event.getThrowable().getClass().getName());
                })
                .onSuccess(event -> {
                    System.out.println("Call succeeded at " + event.getCreationTime());
                })
                .onStateTransition(event -> {
                    System.out.println("CircuitBreaker state changed from " +
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
                                .slidingWindowSynchronizationStrategy(CircuitBreakerConfig.SlidingWindowSynchronizationStrategy.LOCK_FREE)
                                .minimumNumberOfCalls(10)
                                .permittedNumberOfCallsInHalfOpenState(1) // 반열림 상태에서 호출 허용 개수
                                .waitDurationInOpenState(Duration.ofSeconds(10L)) // 열린 상태에서 10초 대기
//                                .ignoreExceptions(RetryableException.class) // 이 옵션을 적용하면 PAY API 서버가 죽었을때 서킷브레이커에 에러 수집이 안된다.
                                .build()
                );
    }

    private static ObjectMapper getObjectMapper() {
        ObjectMapper mapper = (new ObjectMapper())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.INDENT_OUTPUT, true)
                .registerModules(new JavaTimeModule(), new Jdk8Module());
        return mapper;
    }

    private Retry getRetry() {
        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(1000)) // 1초 고정 대기
                .retryExceptions(Exception.class) // 모든 예외에 대해 재시도
                .retryOnException( // FeignClientException, SocketTimeoutExcepion 발생 시 재시도하지 않음
                        throwable -> !(throwable instanceof FeignException.FeignClientException)
                                && !(throwable instanceof SocketTimeoutException))
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

    private RateLimiter getRateLimiter() {
        RateLimiterConfig rateLimiterConfig = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofSeconds(1L))
                .limitForPeriod(10) // 초당 10개 요청으로 제한, 초당 10개 요청으로 제한된 경우 버킷에는 10개의 토큰이 있고 매초 리필됩니다.(토큰 버킷 알고리즘, 버킷이 비면 새 토큰이 생길 때까지 스레드가 대기합니다.)
                .timeoutDuration(Duration.ofSeconds(1L)) //  허가를 기다리는 스레드의 대기시간. 허가를 얻지 못하면 RequestNotPermitted 예외 발생
                .build();
        return RateLimiterRegistry.of(rateLimiterConfig)
                .rateLimiter("payApiClientRateLimiter");
    }


}
