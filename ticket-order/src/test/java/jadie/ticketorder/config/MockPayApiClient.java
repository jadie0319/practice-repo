package jadie.ticketorder.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.net.URI;

public class MockPayApiClient implements PayApiClient {
    private Logger logger = LoggerFactory.getLogger(MockPayApiClient.class);

    @Override
    public ResponseEntity<Void> pay(Integer key) {
        if (key % 2 != 0) { // 홀수면 성공
            logger.info("Key : {}", String.valueOf(key));
            return ResponseEntity.created(URI.create("/pay")).build();
        } else {
            logger.info("Key : {}", String.valueOf(key));
            throw new RuntimeException("Failed pay : " + key);
        }
    }
}
