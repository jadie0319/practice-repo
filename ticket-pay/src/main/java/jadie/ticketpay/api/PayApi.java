package jadie.ticketpay.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PayApi {
    @PostMapping("/pay")
    public ResponseEntity pay(@RequestBody Integer payload) {
        System.out.println("payload : " + payload);
        if (payload % 2 == 0) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.internalServerError().build();
        }

    }
}
