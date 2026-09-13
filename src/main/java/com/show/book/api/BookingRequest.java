package com.show.book.api;

import jakarta.validation.constraints.*;

import java.util.List;

/**
 * Write-scenario request body: the show and seats a customer wants to book.
 */
public record BookingRequest(
        @NotNull @Positive Long customerId,
        @NotNull @Positive Long showId,
        @NotEmpty @Size(max = 10) List<@NotNull @Positive Long> seatIds
) {
}
