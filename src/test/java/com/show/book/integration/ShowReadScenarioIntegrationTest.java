package com.show.book.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Read scenario: "Browse theatres currently running the show (movie selected)
 * in the town, including show timing by a chosen date."
 *
 * <p>Exercises {@code GET /api/v1/shows} end-to-end (controller -> service ->
 * repository -> MySQL) against the fixed seed data in
 * src/main/resources/data.sql. IDs referenced below are deliberately stable -
 * see that file's header comment for the full edge-case rationale.
 */
class ShowReadScenarioIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("returns every Delhi show for movie 1 on 2026-08-15, ordered by time, including sold-out/mini shows")
    void returnsAllShowsForMovieCityAndDate() throws Exception {
        // Delhi/2026-08-15/movie 1 has 8 shows (ids 1,2,3,4,5,6,9,10) at times
        // 09:00, 12:00, 15:00, 17:59, 18:00, 20:00, 22:00, 23:00 - including
        // the mini show (9) and the fully sold-out show (10). Browsing does
        // not filter by seat availability, so both still appear.
        mockMvc.perform(get("/api/v1/shows")
                        .param("movieId", "1")
                        .param("city", "Delhi")
                        .param("date", "2026-08-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(8))
                .andExpect(jsonPath("$[0].time").value("09:00:00"))
                .andExpect(jsonPath("$[0].movieTitle").value("Interstellar"))
                .andExpect(jsonPath("$[0].theatreName").value("XYZ Cinemas - Connaught Place"))
                .andExpect(jsonPath("$[7].time").value("23:00:00"));
    }

    @Test
    @DisplayName("city match is case-insensitive")
    void cityMatchIsCaseInsensitive() throws Exception {
        mockMvc.perform(get("/api/v1/shows")
                        .param("movieId", "1")
                        .param("city", "DELHI")
                        .param("date", "2026-08-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(8));
    }

    @Test
    @DisplayName("a different city on the same date/movie returns only that city's show")
    void filtersToRequestedCity() throws Exception {
        // Show 7: same movie/date as above, but Mumbai instead of Delhi.
        mockMvc.perform(get("/api/v1/shows")
                        .param("movieId", "1")
                        .param("city", "Mumbai")
                        .param("date", "2026-08-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].city").value("Mumbai"))
                .andExpect(jsonPath("$[0].time").value("16:00:00"));
    }

    @Test
    @DisplayName("a different date for the same movie/theatre returns only that date's show")
    void filtersToRequestedDate() throws Exception {
        // Show 8: same movie/theatre as the main dataset, but 2026-08-16.
        mockMvc.perform(get("/api/v1/shows")
                        .param("movieId", "1")
                        .param("city", "Delhi")
                        .param("date", "2026-08-16"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].date").value("2026-08-16"));
    }

    @Test
    @DisplayName("movieId isolates the Hindi edition from the English one, even though the title text is identical")
    void filtersByMovieIdNotTitle() throws Exception {
        // Movie 2 is "Interstellar" (Hindi) - same title string as movie 1, different id.
        mockMvc.perform(get("/api/v1/shows")
                        .param("movieId", "2")
                        .param("city", "Bengaluru")
                        .param("date", "2026-08-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].movieId").value(2))
                .andExpect(jsonPath("$[0].movieTitle").value("Interstellar"));
    }

    @Test
    @DisplayName("a valid movie with no shows anywhere returns an empty list, not an error")
    void movieWithNoShowsReturnsEmptyList() throws Exception {
        // Movie 3 ("The Silent Echo") intentionally has zero shows in the seed data.
        mockMvc.perform(get("/api/v1/shows")
                        .param("movieId", "3")
                        .param("city", "Delhi")
                        .param("date", "2026-08-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("returns an empty list when no show matches the date")
    void returnsEmptyListWhenNoShowMatchesDate() throws Exception {
        mockMvc.perform(get("/api/v1/shows")
                        .param("movieId", "1")
                        .param("city", "Delhi")
                        .param("date", "2099-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
