package com.show.book.discount;

import java.math.BigDecimal;

/**
 * Inputs a {@link DiscountStrategy} needs to price a booking: how many
 * tickets, the pre-discount amount, and the hour the show starts.
 */
public record DiscountContext(
        int ticketCount,
        BigDecimal baseAmount,
        int showHour
) {
}
