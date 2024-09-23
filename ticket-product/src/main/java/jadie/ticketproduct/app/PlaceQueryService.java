package jadie.ticketproduct.app;

import jadie.ticketproduct.infra.api.KakaoPlaceQueryService;
import jadie.ticketproduct.infra.api.KakaoRestApiResponse;
import jadie.ticketproduct.infra.api.KakaoSearchRequest;
import org.springframework.stereotype.Service;

@Service
public class PlaceQueryService {

    private final KakaoPlaceQueryService service;

    public PlaceQueryService(KakaoPlaceQueryService service) {
        this.service = service;
    }

    public KakaoRestApiResponse getPlace(String keyword) {
        KakaoSearchRequest req = KakaoSearchRequest.builder().query(keyword).size(5).build();
        KakaoRestApiResponse kakaoRestApiResponse = service.get(req);
        return kakaoRestApiResponse;
    }
}
