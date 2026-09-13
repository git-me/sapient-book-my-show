package com.show.book.repository;

import com.show.book.domain.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Persistence access for {@link Seat}.
 */
public interface SeatRepository extends JpaRepository<Seat, Long> {
}
