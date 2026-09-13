package com.show.book.discount;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Offer: 50% discount on the third ticket in a booking.
 */
@Component
public class ThirdTicketDiscountStrategy implements DiscountStrategy {

    @Override
    public BigDecimal calculateDiscount(DiscountContext context) {
        if (context.ticketCount() < 3) {
            return BigDecimal.ZERO;
        }

        // 50% discount on the third ticket.
        // Assumption: all tickets have the same base price in this exercise.
        return context.baseAmount()
                .divide(BigDecimal.valueOf(context.ticketCount()), 2, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(0.50));
    }
}
