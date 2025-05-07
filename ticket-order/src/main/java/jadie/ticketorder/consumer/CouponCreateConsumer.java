//package jadie.ticketorder.consumer;
//
//import jadie.ticketorder.domain.Coupon;
//import jadie.ticketorder.domain.CouponRepository;
//import jadie.ticketorder.domain.FailedEvent;
//import jadie.ticketorder.domain.FailedEventRepository;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//@Component
//public class CouponCreateConsumer {
//
//    private final CouponRepository couponRepository;
//    private final FailedEventRepository failedEventRepository;
//    private final Logger logger = LoggerFactory.getLogger(CouponCreateConsumer.class);
//
//    public CouponCreateConsumer(CouponRepository couponRepository, FailedEventRepository failedEventRepository) {
//        this.couponRepository = couponRepository;
//        this.failedEventRepository = failedEventRepository;
//    }
//
//    @KafkaListener(topics = "coupon_create", groupId = "group_1")
//    @Transactional
//    public void listener(Long userId) {
//        logger.info("counsume :: {}, create coupon", userId);
//        couponRepository.save(new Coupon(userId));
////        if (userId % 2 == 0) {
////            throw new IllegalStateException("짝수회원은 쿠폰을 생성할 수 없습니다.");
////        }
//
////        try {
////            couponRepository.save(new Coupon(userId));
////        } catch (Exception e) {
////            logger.error("failed to create coupon:: " + userId);
////            failedEventRepository.save(new FailedEvent(userId));
////        }
//
//    }
//}
