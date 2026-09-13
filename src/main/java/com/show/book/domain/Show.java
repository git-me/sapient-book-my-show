package com.show.book.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * A single scheduled screening of a {@link Movie} at a {@link Theatre} on a
 * given date and time.
 */
@Entity
@Table(name = "shows",
       indexes = {
           @Index(name = "idx_show_movie_date", columnList = "movie_id,show_date"),
           @Index(name = "idx_show_theatre_date", columnList = "theatre_id,show_date")
       })
public class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "theatre_id", nullable = false)
    private Theatre theatre;

    @Column(name = "show_date", nullable = false)
    private LocalDate showDate;

    @Column(name = "show_time", nullable = false)
    private LocalTime showTime;

    protected Show() {
    }

    public Show(Movie movie, Theatre theatre, LocalDate showDate, LocalTime showTime) {
        this.movie = movie;
        this.theatre = theatre;
        this.showDate = showDate;
        this.showTime = showTime;
    }

    public Long getId() { return id; }
    public Movie getMovie() { return movie; }
    public Theatre getTheatre() { return theatre; }
    public LocalDate getShowDate() { return showDate; }
    public LocalTime getShowTime() { return showTime; }
}
