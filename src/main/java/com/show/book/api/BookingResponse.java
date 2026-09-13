package com.show.book.api;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Write-scenario response: the confirmed booking, including the final priced
 * amount after any discounts were applied.
 */
public record BookingResponse(
        Long bookingId,
        Long customerId,
        Long showId,
        List<Long> seatIds,
        BigDecimal totalAmount,
        String status,
        Instant createdAt
) {
}
