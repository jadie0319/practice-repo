package jadie.ticketorder.config;

import feign.Headers;
import feign.RequestLine;
import org.springframework.http.ResponseEntity;

@Headers(value = {"Accept: application/json", "Content-Type: application/json"})
public interface PayApiClient {
    @RequestLine("POST /pay")
    ResponseEntity<Void> pay(Integer payload);
}
