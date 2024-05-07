package jadie.ticketcoupon.app;

import jadie.ticketcoupon.domain.Coupon;
import jadie.ticketcoupon.domain.CouponRepository;
import org.springframework.stereotype.Service;

@Service
public class ApplyService {

    private final CouponRepository couponRepository;

    public ApplyService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public void apply(Long userId) {
        long count = couponRepository.count();
        if (count > 100) {
            return;
        }
        couponRepository.save(new Coupon(userId));
    }
}
