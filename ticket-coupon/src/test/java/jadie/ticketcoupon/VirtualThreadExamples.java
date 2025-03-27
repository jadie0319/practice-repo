package jadie.ticketcoupon;

import org.junit.jupiter.api.Test;
import org.springframework.core.task.VirtualThreadTaskExecutor;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class VirtualThreadExamples {

    @Test
    void test() {
        ThreadFactory factory = Thread.ofVirtual().factory();

        new VirtualThreadTaskExecutor();
        // 기본 가상 스레드 생성 및 실행
        basicVirtualThread();
    }


//    public static void main(String[] args) {
//        // 기본 가상 스레드 생성 및 실행
//        basicVirtualThread();
//
//        // 대량의 가상 스레드 생성 예제
//        massiveVirtualThreads();
//
//        // 가상 스레드 팩토리 사용 예제
//        virtualThreadFactory();
//
//        // ExecutorService와 함께 사용하는 예제
//        virtualThreadWithExecutorService();
//    }

    /**
     * 기본 가상 스레드 생성 및 실행 예제
     */
    private static void basicVirtualThread() {
        System.out.println("\n=== 기본 가상 스레드 예제 ===");

        // Thread.Builder API를 사용한 가상 스레드 생성
        Thread vThread = Thread.ofVirtual().name("simple-virtual-thread").start(() -> {
            System.out.println("실행 중인 스레드: " + Thread.currentThread());
            System.out.println("이것은 가상 스레드입니다: " + Thread.currentThread().isVirtual());

            try {
                // 작업 시뮬레이션
                Thread.sleep(100);
                System.out.println("가상 스레드 작업 완료!");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        try {
            // 가상 스레드가 완료될 때까지 대기
            vThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 대량의 가상 스레드 생성 예제
     */
    private static void massiveVirtualThreads() {
        System.out.println("\n=== 대량의 가상 스레드 예제 ===");

        int threadCount = 10_000; // 10,000개의 가상 스레드 생성
        List<Thread> threads = new ArrayList<>();
        AtomicInteger completedTasks = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        // 많은 수의 가상 스레드 생성
        for (int i = 0; i < threadCount; i++) {
            final int taskId = i;
            Thread vThread = Thread.ofVirtual().name("vthread-" + taskId).start(() -> {
                try {
                    // 각 스레드가 약간의 지연을 갖도록 함
                    Thread.sleep(Duration.ofMillis(50 + (taskId % 50)));
                    completedTasks.incrementAndGet();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            threads.add(vThread);
        }

        // 모든 스레드가 완료될 때까지 대기
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("완료된 작업: " + completedTasks.get() + "/" + threadCount);
        System.out.println("소요 시간: " + (endTime - startTime) + "ms");
    }

    /**
     * 가상 스레드 팩토리 사용 예제
     */
    private static void virtualThreadFactory() {
        System.out.println("\n=== 가상 스레드 팩토리 예제 ===");

        // 가상 스레드를 생성하는 ThreadFactory
        ThreadFactory factory = Thread.ofVirtual().name("custom-vthread-", 1).factory();

        // 팩토리를 사용하여 여러 스레드 생성
        for (int i = 0; i < 5; i++) {
            Thread thread = factory.newThread(() -> {
                System.out.println("팩토리에서 생성된 가상 스레드: " + Thread.currentThread().getName());
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            // 스레드 시작
            thread.start();

            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ExecutorService와 함께 가상 스레드 사용 예제
     */
    private static void virtualThreadWithExecutorService() {
        System.out.println("\n=== ExecutorService와 가상 스레드 예제 ===");

        // 가상 스레드 ExecutorService 생성
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            // 여러 작업 제출
            for (int i = 0; i < 5; i++) {
                final int taskId = i;
                executor.submit(() -> {
                    System.out.println("ExecutorService에서 실행 중인 작업 " + taskId +
                            ", 스레드: " + Thread.currentThread().getName() +
                            ", 가상 스레드: " + Thread.currentThread().isVirtual());

                    try {
                        // 작업 시뮬레이션
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    return "작업 " + taskId + " 완료";
                });
            }

            // ExecutorService가 자동으로 닫힐 때까지 대기
            // (try-with-resources 사용으로 인해)
        }

        System.out.println("모든 ExecutorService 작업이 완료되었습니다.");

        // HTTP 클라이언트 예제를 추가할 수도 있습니다.
        // httpClientExample();
    }

    /**
     * 가상 스레드를 사용한 HTTP 클라이언트 예제
     * 참고: Java 11 이상이 필요합니다.
     */

    private static void httpClientExample() {
        System.out.println("\n=== HTTP 클라이언트와 가상 스레드 예제 ===");

        // 가상 스레드를 사용하는 ExecutorService
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            // HTTP 클라이언트 구성
            HttpClient httpClient = HttpClient.newBuilder()
                    .executor(executor)
                    .build();

            List<URI> urls = List.of(
                    URI.create("https://www.google.com"),
                    URI.create("https://www.github.com"),
                    URI.create("https://www.oracle.com"),
                    URI.create("https://www.openjdk.java.net")
            );

            List<CompletableFuture<String>> futures = urls.stream()
                    .map(url -> {
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(url)
                                .GET()
                                .build();

                        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                                .thenApply(response -> {
                                    System.out.println("응답 받음: " + url + ", 스레드: " +
                                                    Thread.currentThread().getName());
                                    return url + " - 상태 코드: " + response.statusCode();
                                });
                    })
                    .collect(Collectors.toList());

            // 모든 요청이 완료될 때까지 대기
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            // 결과 출력
            futures.forEach(future -> {
                try {
                    System.out.println(future.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

}