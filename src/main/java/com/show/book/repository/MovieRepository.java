package com.show.book.repository;

import com.show.book.domain.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Persistence access for {@link Movie}.
 */
public interface MovieRepository extends JpaRepository<Movie, Long> {
}
