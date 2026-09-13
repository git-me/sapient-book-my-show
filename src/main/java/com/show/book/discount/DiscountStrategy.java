package com.show.book.discount;

import java.math.BigDecimal;

/**
 * Strategy pattern: one pricing offer. {@link service.PricingService} sums the
 * discount from every registered strategy, so offers can be added, removed or
 * tested independently of one another.
 */
public interface DiscountStrategy {

    BigDecimal calculateDiscount(DiscountContext context);
}
