package jadie.ticketproduct.infra.api;

import feign.Headers;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

@Headers(value = {"Authorization: {Authorization}"})
public interface AladinClient {

    @RequestLine("GET /ttb/api/ItemSearch.aspx?ttbkey={ttbkey}&Query={title}&QueryType=Title&MaxResults={MaxResults}&start={start}&SearchTarget=Book&output=js&Version=20131101")
    AladinApiResponseListResponse getBooks(
            @Param("ttbkey") String ttbkey,
            @Param("title") String title,
            @Param("MaxResults") Integer MaxResults,
            @Param("start") Integer start
            );
}
