package jadie.ticketproduct.app;

import jadie.ticketproduct.infra.api.KakaoRestApiResponse;
import org.springframework.stereotype.Service;

@Service
public class SearchPlaceService {

    private final PlaceQueryService placeQueryService;


    public SearchPlaceService(PlaceQueryService placeQueryService) {
        this.placeQueryService = placeQueryService;
    }

    public KakaoRestApiResponse searchPlaces(String keyword) {
        KakaoRestApiResponse response = placeQueryService.getPlace(keyword);
        return response;
    }

}
