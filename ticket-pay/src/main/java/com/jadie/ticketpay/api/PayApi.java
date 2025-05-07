package com.jadie.ticketpay.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PayApi {

    @PostMapping("/pay")
    public ResponseEntity pay(@RequestBody Integer payload) {
        if (payload % 2 == 0) {
            System.out.println("payload success: " + payload);
            return ResponseEntity.ok().build();
        } else {
            System.out.println("payload failed: " + payload);
            return ResponseEntity.internalServerError().build();
        }
    }
}
