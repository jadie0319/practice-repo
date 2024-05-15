package jadie.ticketcoupon.api;

import jadie.ticketcoupon.app.ApplyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class CouponApi {

    private final ApplyService applyService;

    public CouponApi(ApplyService applyService) {
        this.applyService = applyService;
    }

    @PostMapping("/coupon/{userId}")
    public ResponseEntity<?> createCoupon(@PathVariable("userId") Long userId) {
        applyService.apply(userId);
        return ResponseEntity.created(URI.create("/coupon/" + userId)).build();
    }
}
