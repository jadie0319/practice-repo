//package jadie.ticketorder.config;
//
//
//import org.apache.kafka.common.TopicPartition;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
//
//@Configuration
//public class DeadLetterPublishingRecovererConfig {
//    private final Logger logger = LoggerFactory.getLogger(getClass());
//
//    @Bean
//    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(KafkaTemplate<String, String> template) {
//        return new DeadLetterPublishingRecoverer(template,
//                (consumerRecord, ex) -> {
//                    logger.error("=== save coupon_create_DLT, consumerRecord: {}", consumerRecord);
//                    return new TopicPartition("coupon_create_DLT", consumerRecord.partition());
//                });
//    }
//
//
//}
