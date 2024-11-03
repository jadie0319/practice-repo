package jadie.ticketproduct.app;

import jadie.ticketproduct.infra.api.AladinApiResponseListResponse;
import jadie.ticketproduct.infra.api.AladinApiResponseResponse;
import jadie.ticketproduct.infra.api.AladinClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AladinService {

    private final AladinClient client;

    public AladinService(AladinClient client) {
        // ttbkoola9761654001
        this.client = client;
    }

    public Page<AladinApiResponseResponse> get(String title, Pageable pageable) {
        Integer MaxResults = pageable.getPageSize();
        Integer start = pageable.getPageNumber() + 1;
        AladinApiResponseListResponse result = client.getBooks("ttbkoola9761654001", title, MaxResults, start);
        System.out.println(result);
        return new PageImpl<>(result.item(), pageable, result.item().size());
    }
}
