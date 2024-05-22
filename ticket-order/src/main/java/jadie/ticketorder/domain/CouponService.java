package jadie.ticketorder.domain;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public void findByUserIdSorted(Long userId) {
        Sort sort = Sort.by("userId").descending();
        couponRepository.findByUserId(userId, sort);
    }
}
