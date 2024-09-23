package jadie.ticketproduct.infra.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoRestApiResponseItem(
        @JsonProperty("place_name") String placeName,
        @JsonProperty("phone") String phone,
        @JsonProperty("address_name") String addressName,
        @JsonProperty("road_address_name") String roadAddressName) {
}
