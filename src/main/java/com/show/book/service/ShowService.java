package com.show.book.service;

import com.show.book.repository.ShowRepository;
import com.show.book.api.ShowResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Read scenario: browse the theatres/shows currently running a movie in a
 * given city on a given date.
 */
@Service
public class ShowService {

    private final ShowRepository showRepository;

    public ShowService(ShowRepository showRepository) {
        this.showRepository = showRepository;
    }

    @Transactional(readOnly = true)
    public List<ShowResponse> findShows(Long movieId, String city, LocalDate date) {
        return showRepository.findShows(movieId, city, date)
                .stream()
                .map(show -> new ShowResponse(
                        show.getId(),
                        show.getMovie().getId(),
                        show.getMovie().getTitle(),
                        show.getTheatre().getId(),
                        show.getTheatre().getName(),
                        show.getTheatre().getCity(),
                        show.getShowDate(),
                        show.getShowTime()
                ))
                .toList();
    }
}
