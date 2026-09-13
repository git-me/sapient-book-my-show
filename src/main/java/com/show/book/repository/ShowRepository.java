package com.show.book.repository;

import com.show.book.domain.Show;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * Persistence access for {@link Show}, including the
 * read-scenario lookup by movie, city and date.
 */
public interface ShowRepository extends JpaRepository<Show, Long> {

    @Query("""
        select s from Show s
        join fetch s.movie m
        join fetch s.theatre t
        where m.id = :movieId
          and lower(t.city) = lower(:city)
          and s.showDate = :showDate
        order by t.name, s.showTime
        """)
    List<Show> findShows(@Param("movieId") Long movieId,
                         @Param("city") String city,
                         @Param("showDate") LocalDate showDate);
}
