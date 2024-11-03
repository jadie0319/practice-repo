package jadie.ticketproduct.infra.api;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.Logger;
import feign.Request;
import feign.Retryer;
import feign.codec.Encoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import io.github.resilience4j.feign.FeignDecorators;
import io.github.resilience4j.feign.Resilience4jFeign;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

@Configuration
public class AladinClientConfig {


    @Bean
    public AladinClient aladinClient() {
        FeignDecorators decorators = FeignDecorators.builder()
                .build();
        ObjectMapper mapper = (new ObjectMapper())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.INDENT_OUTPUT, true)
                .registerModules(new JavaTimeModule(), new Jdk8Module());

        return Resilience4jFeign.builder(decorators)
                .client(new OkHttpClient())
                .encoder(new JacksonEncoder(mapper))
                .decoder(new JacksonDecoder(mapper))
                .options(new Request.Options(3000, TimeUnit.MILLISECONDS, 10000, TimeUnit.MILLISECONDS, true))
                .retryer(new Retryer.Default(100, SECONDS.toMillis(1), 2))
                .logger(new Slf4jLogger(AladinClient.class))
                .logLevel(Logger.Level.FULL)
                .target(AladinClient.class, "http://www.aladin.co.kr");
    }
}
