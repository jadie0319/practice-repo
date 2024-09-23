package jadie.ticketproduct.infra.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Primary
@Service
public class KakaoPlaceQueryServiceWebClient implements KakaoPlaceQueryService{

    private final KakaoClient kakaoClient;
    private final String key;

    public KakaoPlaceQueryServiceWebClient(KakaoClient kakaoClient, @Value("${kakao.auth.key}") String key) {
        this.kakaoClient = kakaoClient;
        this.key = key;
    }

    public KakaoRestApiResponse get(KakaoSearchRequest req) {

        // ObjectMapper, WebClient 코드리뷰 해준것 코드 확인용 코드.
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        WebClient webClient = WebClient.builder()
                .codecs(configurer -> {
                            configurer
                                    .defaultCodecs()
                                    .jackson2JsonEncoder(
                                            new Jackson2JsonEncoder(mapper, MediaType.APPLICATION_JSON));
                            configurer
                                    .defaultCodecs()
                                    .jackson2JsonDecoder(
                                            new Jackson2JsonDecoder(mapper, MediaType.APPLICATION_JSON));
                        }
                ).build();
        KakaoRestApiResponse response = webClient.get()
                .uri("https://dapi.kakao.com/v2/local/search/keyword.json?query=" + "신당동" + "&size=5")
                .header("Authorization", key)
                .retrieve()
                .bodyToMono(KakaoRestApiResponse.class)
                .block();
        return response;
    }

}
