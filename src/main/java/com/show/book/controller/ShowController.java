package com.show.book.controller;

import com.show.book.api.ShowResponse;
import com.show.book.service.ShowService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Read scenario: HTTP entry point for browsing shows.
 */
@RestController
@RequestMapping("/api/v1/shows")
public class ShowController {

    private final ShowService showService;

    public ShowController(ShowService showService) {
        this.showService = showService;
    }

    @GetMapping
    public List<ShowResponse> findShows(
            @RequestParam Long movieId,
            @RequestParam String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return showService.findShows(movieId, city, date);
    }
}
