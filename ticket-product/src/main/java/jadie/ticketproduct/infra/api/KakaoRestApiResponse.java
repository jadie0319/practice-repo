package jadie.ticketproduct.infra.api;

import java.util.List;
import java.util.stream.Collectors;


public record KakaoRestApiResponse(List<KakaoRestApiResponseItem> documents) {

    public List<Place> getApiResults() {
        return documents.stream()
                .map(document -> Place.builder()
                        .placeName(convert(document.placeName()))
                        //.address(document.addressName())
                        .build())
                .collect(Collectors.toList());
    }

    private String convert(String placeName) {
        return placeName.trim()
                .replace(" ", "");
    }
}
