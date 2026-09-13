package com.show.book.repository;

import com.show.book.domain.ShowSeat;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Persistence access for {@link ShowSeat} - the
 * per-show seat inventory row that seat availability and booking are checked
 * and mutated against.
 */
public interface ShowSeatRepository extends JpaRepository<ShowSeat, Long> {

    @Query("""
        select ss from ShowSeat ss
        join fetch ss.seat
        where ss.show.id = :showId
          and ss.seat.id in :seatIds
        order by ss.seat.id
        """)
    List<ShowSeat> findSeatsForBooking(@Param("showId") Long showId,
                                       @Param("seatIds") List<Long> seatIds);

    long countByShowId(Long showId);
}
