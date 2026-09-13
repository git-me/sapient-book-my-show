package com.show.book.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.show.book.api.BookingRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Write scenario: "Book movie tickets by selecting a theatre, timing and
 * preferred seats for the day."
 *
 * <p>Exercises {@code POST /api/v1/bookings} end-to-end against MySQL,
 * covering the pricing offers (including the exact hour boundary of the
 * afternoon discount), seat locking, and the Idempotency-Key contract - using
 * the fixed seed data in src/main/resources/data.sql. Each test method uses a
 * disjoint (show, seat) combination so the tests don't interfere with each
 * other regardless of execution order.
 */
class BookingWriteScenarioIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("booking 3 seats on show 2 (15:00) stacks both discounts: 600 -> 380.00")
    void bookingThreeSeatsOnAfternoonShowStacksBothDiscounts() throws Exception {
        assertBookingTotal(101L, 2L, List.of(1L, 2L, 3L), "wr-show2-stack", "380.00");
    }

    @Test
    @DisplayName("show 1 (12:00) is the INCLUDED start edge of the afternoon window: 200 -> 160.00")
    void afternoonWindowIncludedStartEdge() throws Exception {
        assertBookingTotal(102L, 1L, List.of(4L), "wr-show1-edge-start", "160.00");
    }

    @Test
    @DisplayName("show 3 (17:59) is the INCLUDED end edge of the afternoon window: 200 -> 160.00")
    void afternoonWindowIncludedEndEdge() throws Exception {
        assertBookingTotal(103L, 3L, List.of(5L), "wr-show3-edge-end", "160.00");
    }

    @Test
    @DisplayName("show 4 (18:00), one hour later than show 3, is EXCLUDED from the afternoon window: 200 -> 200.00")
    void afternoonWindowExcludedJustAfter() throws Exception {
        assertBookingTotal(104L, 4L, List.of(6L), "wr-show4-edge-excluded", "200.00");
    }

    @Test
    @DisplayName("booking 3 seats on show 5 (20:00) only applies the third-ticket discount: 600 -> 500.00")
    void bookingThreeSeatsOnEveningShowOnlyAppliesThirdTicketDiscount() throws Exception {
        assertBookingTotal(105L, 5L, List.of(1L, 2L, 3L), "wr-show5-third-only", "500.00");
    }

    @Test
    @DisplayName("a single seat on a non-afternoon show gets no discount at all: 200 -> 200.00")
    void singleTicketOnMorningShowHasNoDiscount() throws Exception {
        assertBookingTotal(106L, 6L, List.of(7L), "wr-show6-baseline", "200.00");
    }

    @Test
    @DisplayName("the last remaining seat on the mini 2-seat show can still be booked")
    void lastRemainingSeatOnMiniShowCanBeBooked() throws Exception {
        // Show 9 has only two seats total (BAL1 id=11, BAL2 id=12); BAL1 is
        // already booked in the seed data, so this exercises "one seat left".
        assertBookingTotal(107L, 9L, List.of(12L), "wr-show9-last-seat", "200.00");
    }

    @Test
    @DisplayName("the mini show's already-booked seat is rejected immediately, no setup required")
    void miniShowAlreadyBookedSeatIsRejectedImmediately() throws Exception {
        // Seed data already marks seat 11 (BAL1) BOOKED on show 9.
        BookingRequest request = new BookingRequest(108L, 9L, List.of(11L));

        mockMvc.perform(post("/api/v1/bookings")
                        .header("Idempotency-Key", "wr-show9-conflict")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("the fully sold-out show rejects any seat immediately, no setup required")
    void fullySoldOutShowIsRejectedImmediately() throws Exception {
        // Seed data already marks all 10 seats BOOKED on show 10.
        BookingRequest request = new BookingRequest(109L, 10L, List.of(1L));

        mockMvc.perform(post("/api/v1/bookings")
                        .header("Idempotency-Key", "wr-show10-conflict")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("booking on the Hindi-edition show (movie 2, Bengaluru, 15:00) still gets the afternoon discount")
    void hindiEditionShowStillGetsAfternoonDiscount() throws Exception {
        assertBookingTotal(110L, 11L, List.of(1L), "wr-show11-hindi", "160.00");
    }

    @Test
    @DisplayName("repeating a request with the same Idempotency-Key returns the original booking, not a duplicate")
    void repeatedRequestWithSameIdempotencyKeyIsNotDuplicated() throws Exception {
        BookingRequest request = new BookingRequest(111L, 6L, List.of(8L));
        String idempotencyKey = "wr-show6-idempotency";
        String body = objectMapper.writeValueAsString(request);

        String firstResponse = mockMvc.perform(post("/api/v1/bookings")
                        .header("Idempotency-Key", idempotencyKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String secondResponse = mockMvc.perform(post("/api/v1/bookings")
                        .header("Idempotency-Key", idempotencyKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long firstBookingId = objectMapper.readTree(firstResponse).get("bookingId").asLong();
        Long secondBookingId = objectMapper.readTree(secondResponse).get("bookingId").asLong();

        assertThat(secondBookingId).isEqualTo(firstBookingId);
    }

    private void assertBookingTotal(Long customerId, Long showId, List<Long> seatIds,
                                     String idempotencyKey, String expectedTotal) throws Exception {
        BookingRequest request = new BookingRequest(customerId, showId, seatIds);

        mockMvc.perform(post("/api/v1/bookings")
                        .header("Idempotency-Key", idempotencyKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.totalAmount").value(Double.parseDouble(expectedTotal)));
    }
}
