package com.show.book.discount;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Offer: 20% discount on tickets booked for an afternoon show (12:00-17:59).
 */
@Component
public class AfternoonDiscountStrategy implements DiscountStrategy {

    @Override
    public BigDecimal calculateDiscount(DiscountContext context) {
        // Assumption: afternoon means 12:00 through 17:59.
        if (context.showHour() < 12 || context.showHour() >= 18) {
            return BigDecimal.ZERO;
        }

        return context.baseAmount().multiply(BigDecimal.valueOf(0.20));
    }
}
