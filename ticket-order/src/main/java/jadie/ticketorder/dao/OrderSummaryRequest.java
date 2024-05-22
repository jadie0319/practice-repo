package jadie.ticketorder.dao;

import java.time.LocalDateTime;

public record OrderSummaryRequest(
        Long orderId,
        String ordererName,
        LocalDateTime from,
        LocalDateTime to
) {
}
