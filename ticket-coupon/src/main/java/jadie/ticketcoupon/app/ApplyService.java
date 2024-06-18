package jadie.ticketcoupon.app;

import jadie.ticketcoupon.domain.AppliedUserRepository;
import jadie.ticketcoupon.domain.Coupon;
import jadie.ticketcoupon.domain.CouponCountRepository;
import jadie.ticketcoupon.domain.CouponRepository;
import jadie.ticketcoupon.producer.CouponCreateProducer;
import org.springframework.stereotype.Service;

@Service
public class ApplyService {

    private final CouponRepository couponRepository;
    private final CouponCountRepository couponCountRepository;
    private final CouponCreateProducer couponCreateProducer;
    private final AppliedUserRepository appliedUserRepository;

    public ApplyService(CouponRepository couponRepository, CouponCountRepository couponCountRepository,
                        CouponCreateProducer couponCreateProducer, AppliedUserRepository appliedUserRepository) {
        this.couponRepository = couponRepository;
        this.couponCountRepository = couponCountRepository;
        this.couponCreateProducer = couponCreateProducer;
        this.appliedUserRepository = appliedUserRepository;
    }

    // 인프런 강의 코드
    public void apply(Long userId) {
        //long count = couponRepository.count();
        //Long count = couponCountRepository.increment();
        Long apply = appliedUserRepository.add(userId);
        if (apply != 1) {
            // 이미 쿠폰을 발급받은 유저
            return;
        }

        Long count = couponCountRepository.increment();

        if (count > 100) {
            return;
        }
        //couponRepository.save(new Coupon(userId));
        couponCreateProducer.create(userId);
    }
}
