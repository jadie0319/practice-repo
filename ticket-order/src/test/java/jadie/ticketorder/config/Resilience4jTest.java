package jadie.ticketorder.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import feign.Feign;
import feign.FeignException;
import feign.Logger;
import feign.Retryer;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.util.function.Function;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

public class Resilience4jTest {

//    private WireMockServer wireMock;
//    @BeforeEach
//    public void setUp() {
//        wireMock = new WireMockServer(wireMockConfig().port(8080));
//        wireMock.start();
//        WireMock.configureFor("localhost", 8080);
//    }
//    @AfterEach
//    void tearDown() {
//        wireMock.stop();
//    }



    @DisplayName("Resilience4j 테스트")
    @Test
    void testRateLimiter() throws InterruptedException {
//        wireMock.stubFor(
//                WireMock.post("/sigong/exam/file.do")
//                        .withRequestBody(equalToJson("{\"key\":\"0\"}", true, true))
//                        .willReturn(
//                                aResponse().withStatus(200)
//                        )
//        );
//        wireMock.stubFor(
//                WireMock.post("/sigong/exam/file.do")
//                        .withRequestBody(equalToJson("{\"key\":\"1\"}", true, true))
//                        .willReturn(
//                                aResponse().withStatus(500)
//                        )
//        );

        CircuitBreaker circuitBreaker = getCircuitBreaker();
        addCircuitBreakerMonitoring(circuitBreaker);

        RateLimiter rateLimiter = getRateLimiter();
        addRateLimiterMonitor(rateLimiter);

        Retry retry = getRetry();
        addRetryMonitor(retry);

        ObjectMapper mapper = (new ObjectMapper())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.INDENT_OUTPUT, true)
                .registerModules(new JavaTimeModule(), new Jdk8Module());

//        PayApiClient examSvcApiClient = Feign.builder()
//                .encoder(new JacksonEncoder(mapper))
//                .decoder(new JacksonDecoder(mapper))
//                .logger(new Slf4jLogger(PayApiClient.class))
//                .retryer(Retryer.NEVER_RETRY)
//                .logLevel(Logger.Level.FULL)
//                //.errorDecoder(new MockFeignErrorDecoder())
//                .target(PayApiClient.class, "http://localhost:8081");


        MockPayApiClient mockPayApiClient = new MockPayApiClient();

//        Function<Integer, ResponseEntity<Void>> decoratedFunction = CircuitBreaker.decorateFunction(
//                circuitBreaker,
//                RateLimiter.decorateFunction(
//                        rateLimiter,
//                        Retry.decorateFunction(
//                                retry,
//                                (Integer event) -> mockPayApiClient.pay(event)
//                        )
//                )
//        );

        // retry 시도가 서킷브레이커에 누적됨
        Function<Integer, ResponseEntity<Void>> decoratedFunction = Retry.decorateFunction(
                retry,
                RateLimiter.decorateFunction(
                        rateLimiter,
                        CircuitBreaker.decorateFunction(
                                circuitBreaker,
                                (Integer event) -> mockPayApiClient.pay(event)
                        )
                )
        );


        for (int i = 0; i < 10; i++) {
            System.out.println();
            System.out.println();

            try {
                int index = i % 2;
                // 짝수면 성공, 홀수면 실패한다.
                System.out.println("Request #" + i + ": " + index);
                ResponseEntity<Void> response = decoratedFunction.apply(index);
            } catch (RequestNotPermitted e) {
                System.out.println("RequestNotPermitted Request #" + i + " - RequestNotPermitted: " + e.getMessage());
            } catch (CallNotPermittedException e) {
                System.out.println("CallNotPermittedException Request #" + i + " - CallNotPermittedException: " + e.getMessage());
            } catch (FeignException e) {
                System.out.println("FeignException Request #" + i + " - FeignException Retry: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Exception Request #" + i + " - Exception: " + e.getMessage());
            }
            // 여기서 예외를 catch 안하면 예외 뱉고 테스트 종료되길래. 근데 catch 하면 재시도도 안할까봐 catch 안했는데 catch 해야 됐었다...
            // feign 통해서 호출(wiremock 사용) 하는건 예외 발생해도 테스트 종료 안되는데, feign 안쓰면 catch 해줘야 테스트가 종료 안된다.
//            catch (FailedCreateFileException e) {
//                System.out.println("FailedCreateFileException Request #" + i + " - FailedCreateFileException: " + e.getMessage());
//            }


//            final int index = i;
//            CompletableFuture<ResponseEntity<Void>> future = CompletableFuture.supplyAsync(() -> {
//                try {
//                    CreateExamFileRequest req = new CreateExamFileRequest(String.valueOf(index), EventType.EXAM_ID.name(), ChangeType.INSERT.name());
//                    ResponseEntity<Void> response = decoratedFunction.apply(req);
//                    return response;
//                } catch (RequestNotPermitted e) {
//                    System.out.println("RequestNotPermitted Request #" + index + " - RequestNotPermitted: " + e.getMessage());
//                } catch (CallNotPermittedException e) {
//                    System.out.println("CallNotPermittedException Request #" + index + " - CallNotPermittedException: " + e.getMessage());
//                } catch (FeignException e) {
//                    System.out.println("FeignException Request #" + index + " - FeignException Retry: " + e.getMessage());
//                }
//                return null;
//            }, executor);
//            futures.add(future);
//
//            // 각 요청 사이에 짧은 지연
//            Thread.sleep(100);
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
                .onSuccess(e -> System.out.println("\nRETRY - Successfully added retry event: " + e))
                .onError(e -> System.out.println("\nRETRY - Error: " + e));
//                .onRetry(e -> System.out.println("\nRETRY - Retry event: " + e));
    }

    private void addRateLimiterMonitor(RateLimiter rateLimiter) {
        rateLimiter.getEventPublisher()
                .onSuccess(event -> {
                    System.out.println("RL - success: "+ " AvailablePermissions: " + rateLimiter.getMetrics().getAvailablePermissions() + " NumberOfWaitingThreads: " + rateLimiter.getMetrics().getNumberOfWaitingThreads());
                })
                .onFailure(throwable -> {
                    System.out.println("RL - failed: " + throwable + " AvailablePermissions: " + rateLimiter.getMetrics().getAvailablePermissions() + " NumberOfWaitingThreads: " + rateLimiter.getMetrics().getNumberOfWaitingThreads());
                });
    }

    private void addCircuitBreakerMonitoring(CircuitBreaker circuitBreaker) {
        circuitBreaker.getEventPublisher()
                .onCallNotPermitted(event -> {
                    System.out.println("CB - Call not permitted at " + event.getCreationTime());
                })
                .onError(event -> {
                    System.out.println("CB - Call errorCall: " + circuitBreaker.getMetrics().getNumberOfSuccessfulCalls() + " failedCall: " + circuitBreaker.getMetrics().getNumberOfFailedCalls() + " notPermittedCall:" + circuitBreaker.getMetrics().getNumberOfNotPermittedCalls());
                    //System.out.println("CB - Call failed at " + event.getCreationTime() + " with exception: " + event.getThrowable().getClass().getName());
                })
                .onSuccess(event -> {
                    System.out.println("CB - Call successCall: " + circuitBreaker.getMetrics().getNumberOfSuccessfulCalls() + " failedCall: " + circuitBreaker.getMetrics().getNumberOfFailedCalls() + " notPermittedCall:" + circuitBreaker.getMetrics().getNumberOfNotPermittedCalls());
                })
                .onStateTransition(event -> {
                    System.out.println("CB - CircuitBreaker state changed from " +
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
                .limitRefreshPeriod(Duration.ofMillis(500))
                .limitForPeriod(10) // 초당 10개 요청으로 제한, 초당 10개 요청으로 제한된 경우 버킷에는 10개의 토큰이 있고 매초 리필됩니다.(토큰 버킷 알고리즘, 버킷이 비면 새 토큰이 생길 때까지 스레드가 대기합니다.)
                .timeoutDuration(Duration.ofSeconds(1L)) //  허가를 기다리는 스레드의 대기시간. 허가를 얻지 못하면 RequestNotPermitted 예외 발생
                .build();
        return RateLimiterRegistry.of(rateLimiterConfig)
                .rateLimiter("payApiClientRateLimiter");
    }


}
