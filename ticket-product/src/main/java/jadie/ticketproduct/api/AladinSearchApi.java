package jadie.ticketproduct.api;


import jadie.ticketproduct.app.AladinService;
import jadie.ticketproduct.app.SearchPlaceService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AladinSearchApi {


    private final AladinService aladinService;

    public AladinSearchApi(AladinService aladinService) {
        this.aladinService = aladinService;
    }

    @GetMapping("/search")
    public ResponseEntity getPlaces(String title, Pageable pageable) {
        return ResponseEntity.ok(aladinService.get(title, pageable));
    }

}
