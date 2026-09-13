package com.show.book.api;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Read-scenario response: a single show (movie + theatre + timing) matching
 * the caller's browse query.
 */
public record ShowResponse(
        Long showId,
        Long movieId,
        String movieTitle,
        Long theatreId,
        String theatreName,
        String city,
        LocalDate date,
        LocalTime time
) {
}
