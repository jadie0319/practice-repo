package jadie.ticketproduct.infra.api;

import feign.Headers;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

@Headers(value = {"Authorization: {Authorization}", "Accept: application/json", "Content-Type: application/json"})
public interface KakaoClient {

    @RequestLine("GET /v2/local/search/keyword.json")
    KakaoRestApiResponse getPlaces(@Param("Authorization") String authHeader, @QueryMap KakaoSearchRequest request);
}
