//package jadie.ticketorder.consumer;
//
//import jadie.ticketorder.domain.Coupon;
//import jadie.ticketorder.domain.CouponRepository;
//import jadie.ticketorder.domain.FailedEventRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//@Component
//public class CouponCreateDLTConsumer {
//
//    private final CouponRepository couponRepository;
//    private final FailedEventRepository failedEventRepository;
//    private final Logger logger = LoggerFactory.getLogger(CouponCreateDLTConsumer.class);
//
//    public CouponCreateDLTConsumer(CouponRepository couponRepository, FailedEventRepository failedEventRepository) {
//        this.couponRepository = couponRepository;
//        this.failedEventRepository = failedEventRepository;
//    }
//
//    @KafkaListener(topics = "coupon_create_DLT", groupId = "group_1")
//    @Transactional
//    public void listener(Long userId) {
//        logger.error("DLT  counsume :: {}, create coupon", userId);
//
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
