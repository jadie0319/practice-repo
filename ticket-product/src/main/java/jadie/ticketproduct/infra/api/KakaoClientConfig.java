package jadie.ticketproduct.infra.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Logger;
import feign.Request;
import feign.Retryer;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import io.github.resilience4j.feign.FeignDecorators;
import io.github.resilience4j.feign.Resilience4jFeign;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

@Configuration
public class KakaoClientConfig {
    private final ObjectMapper mapper;

    public KakaoClientConfig(ObjectMapper objectMapper) {
        this.mapper = objectMapper;
    }

    @Bean
    public KakaoClient kakaoClient() {
        FeignDecorators decorators = FeignDecorators.builder()
                .build();

        return Resilience4jFeign.builder(decorators)
                .client(new OkHttpClient())
                .encoder(new JacksonEncoder(mapper))
                .decoder(new JacksonDecoder(mapper))
                .options(new Request.Options(3000, TimeUnit.MILLISECONDS, 10000, TimeUnit.MILLISECONDS, true))
                .retryer(new Retryer.Default(100, SECONDS.toMillis(1), 2))
                .logger(new Slf4jLogger(KakaoClient.class))
                .logLevel(Logger.Level.FULL)
                .target(KakaoClient.class, "https://dapi.kakao.com");
    }
}
