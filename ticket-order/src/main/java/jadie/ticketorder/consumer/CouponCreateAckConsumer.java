//package jadie.ticketorder.consumer;
//
//import jadie.ticketorder.domain.Coupon;
//import jadie.ticketorder.domain.CouponRepository;
//import jadie.ticketorder.domain.FailedEventRepository;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.listener.AcknowledgingMessageListener;
//import org.springframework.kafka.support.Acknowledgment;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.Duration;
//
//@Component
//public class CouponCreateAckConsumer implements AcknowledgingMessageListener<String, Long> {
//
//    private final CouponRepository couponRepository;
//    private final FailedEventRepository failedEventRepository;
//    private final Logger logger = LoggerFactory.getLogger(CouponCreateAckConsumer.class);
//
//    public CouponCreateAckConsumer(CouponRepository couponRepository, FailedEventRepository failedEventRepository) {
//        this.couponRepository = couponRepository;
//        this.failedEventRepository = failedEventRepository;
//    }
//
//    @Override
//    @KafkaListener(topics = "coupon_create", groupId = "group_1")
//    @Transactional
//    public void onMessage(ConsumerRecord<String, Long> data, Acknowledgment ack) {
//        Long userId = data.value();
//
//        // DB 에 메시지 저장. STATE NOT_COMPLETE
//        // DB 에 메시지가 존재하면 저장X
//
//        logger.info("counsume :: {}, create coupon", userId);
//        couponRepository.save(new Coupon(userId));
////        if (userId % 2 == 0) {
////            //ack.nack(Duration.ofSeconds(10));
////            throw new IllegalStateException("짝수회원은 쿠폰을 생성할 수 없습니다.");
////        }
//
//        ack.acknowledge();
//        // DB 메시지 상태 변경. STATE COMPLETE
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
