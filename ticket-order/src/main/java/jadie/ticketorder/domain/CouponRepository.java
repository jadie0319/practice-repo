package jadie.ticketorder.domain;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    List<Coupon> findByUserIdOrderByIdDesc(Long userId);
    List<Coupon> findByUserId(Long userId, Sort sort);
}
