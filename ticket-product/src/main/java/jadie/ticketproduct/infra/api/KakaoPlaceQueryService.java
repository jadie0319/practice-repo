package jadie.ticketproduct.infra.api;



public interface KakaoPlaceQueryService {
    KakaoRestApiResponse get(KakaoSearchRequest req);
}
