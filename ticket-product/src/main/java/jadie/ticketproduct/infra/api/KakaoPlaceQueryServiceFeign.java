package jadie.ticketproduct.infra.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KakaoPlaceQueryServiceFeign implements KakaoPlaceQueryService{

    private final KakaoClient kakaoClient;
    private final String key;

    public KakaoPlaceQueryServiceFeign(KakaoClient kakaoClient, @Value("${kakao.auth.key}") String key) {
        this.kakaoClient = kakaoClient;
        this.key = key;
    }

    public KakaoRestApiResponse get(KakaoSearchRequest req) {
        KakaoRestApiResponse places = kakaoClient.getPlaces(key, req);
        return places;
    }

}
