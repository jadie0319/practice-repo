//package jadie.ticketorder.config;
//
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.common.serialization.LongDeserializer;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.core.ConsumerFactory;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//import org.springframework.kafka.listener.ContainerProperties;
//import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
//import org.springframework.kafka.listener.DefaultErrorHandler;
//import org.springframework.util.backoff.BackOff;
//import org.springframework.util.backoff.FixedBackOff;
//
//import java.net.SocketTimeoutException;
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//public class KafkaConsumerConfig {
//
//    @Bean
//    public ConsumerFactory<String, Long> consumerFactory() {
//        Map<String, Object> config = new HashMap<>();
//
//        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
//        config.put(ConsumerConfig.GROUP_ID_CONFIG, "group_1");
//        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
//        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);
//
//        return new DefaultKafkaConsumerFactory<>(config);
//    }
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, Long> kafkaListenerContainerFactory(
//            @Qualifier("deadLetterPublishingRecoverer") DeadLetterPublishingRecoverer deadLetterPublishingRecoverer) {
//        ConcurrentKafkaListenerContainerFactory<String, Long> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(consumerFactory());
//        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
//        factory.setConcurrency(1);
//        factory.setCommonErrorHandler(couponCreateErrorHandler(deadLetterPublishingRecoverer));
//        return factory;
//    }
//
//    private DefaultErrorHandler couponCreateErrorHandler(DeadLetterPublishingRecoverer deadLetterPublishingRecoverer) {
//        BackOff fixedBackOff = new FixedBackOff(1000L, 3L);
//        DefaultErrorHandler errorHandler = new DefaultErrorHandler(deadLetterPublishingRecoverer, fixedBackOff);
//        errorHandler.addRetryableExceptions(SocketTimeoutException.class);
//        errorHandler.addNotRetryableExceptions(NullPointerException.class);
//        return errorHandler;
//    }
//}
