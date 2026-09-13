package com.show.book.repository;

import com.show.book.domain.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Persistence access for {@link Theatre}.
 */
public interface TheatreRepository extends JpaRepository<Theatre, Long> {
}
