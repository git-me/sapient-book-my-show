package com.show.book.domain;

import jakarta.persistence.*;

/**
 * A movie that can be shown across theatres/cities/languages.
 */
@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String language;
    private String genre;

    protected Movie() {
    }

    public Movie(String title, String language, String genre) {
        this.title = title;
        this.language = language;
        this.genre = genre;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getLanguage() { return language; }
    public String getGenre() { return genre; }
}
