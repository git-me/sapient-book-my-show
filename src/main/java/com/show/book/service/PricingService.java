package com.show.book.service;

import com.show.book.discount.DiscountStrategy;
import com.show.book.discount.DiscountContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Prices a set of tickets by applying every registered {@link DiscountStrategy}
 * (Strategy pattern) to the base amount. Adding a new offer means adding a new
 * {@code @Component} implementing {@link DiscountStrategy} - this class does not
 * need to change.
 */
@Service
public class PricingService {

    private static final BigDecimal TICKET_PRICE = BigDecimal.valueOf(200);

    private final List<DiscountStrategy> discountStrategies;

    public PricingService(List<DiscountStrategy> discountStrategies) {
        this.discountStrategies = discountStrategies;
    }

    public BigDecimal calculateTotal(int ticketCount, int showHour) {
        BigDecimal baseAmount = TICKET_PRICE.multiply(BigDecimal.valueOf(ticketCount));

        DiscountContext context = new DiscountContext(ticketCount, baseAmount, showHour);

        BigDecimal totalDiscount = discountStrategies.stream()
                .map(strategy -> strategy.calculateDiscount(context))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return baseAmount.subtract(totalDiscount)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
