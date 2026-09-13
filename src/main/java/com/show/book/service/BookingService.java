package com.show.book.service;

import com.show.book.domain.*;
import com.show.book.repository.BookingRepository;
import com.show.book.repository.ShowRepository;
import com.show.book.repository.ShowSeatRepository;
import com.show.book.api.BookingRequest;
import com.show.book.api.BookingResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.List;

/**
 * Write scenario: book seats for a show.
 *
 * <p>Concurrency and correctness are handled with three mechanisms:
 * <ul>
 *   <li>Idempotency - a repeat request with the same {@code Idempotency-Key}
 *       returns the original booking instead of creating a second one.</li>
 *   <li>Optimistic locking - {@link ShowSeat} carries a
 *       {@code @Version} column, so two concurrent requests racing for the same
 *       seat cannot both succeed.</li>
 *   <li>A unique constraint on the idempotency key as a last-resort guard if two
 *       identical requests race past the initial lookup.</li>
 * </ul>
 */
@Service
public class BookingService {

    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;
    private final BookingRepository bookingRepository;
    private final PricingService pricingService;

    public BookingService(ShowRepository showRepository,
                          ShowSeatRepository showSeatRepository,
                          BookingRepository bookingRepository,
                          PricingService pricingService) {
        this.showRepository = showRepository;
        this.showSeatRepository = showSeatRepository;
        this.bookingRepository = bookingRepository;
        this.pricingService = pricingService;
    }

    @Transactional
    public BookingResponse book(String idempotencyKey, BookingRequest request) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("Idempotency-Key header is required");
        }

        // Fast path for retries of an already completed request.
        var existing = bookingRepository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            return toResponse(existing.get());
        }

        if (request.seatIds().stream().distinct().count() != request.seatIds().size()) {
            throw new IllegalArgumentException("Duplicate seat IDs are not allowed");
        }

        Show show = showRepository.findById(request.showId())
                .orElseThrow(() -> new IllegalArgumentException("Show not found: " + request.showId()));

        List<ShowSeat> showSeats =
                showSeatRepository.findSeatsForBooking(show.getId(), request.seatIds());

        if (showSeats.size() != request.seatIds().size()) {
            throw new IllegalArgumentException("One or more selected seats do not exist for this show");
        }

        showSeats.forEach(showSeat -> {
            if (showSeat.getStatus() != SeatStatus.AVAILABLE) {
                throw new IllegalStateException(
                        "Seat already booked: " + showSeat.getSeat().getSeatNumber());
            }
        });

        showSeats.forEach(ShowSeat::book);

        var total = pricingService.calculateTotal(
                showSeats.size(),
                show.getShowTime().getHour());

        Booking booking = new Booking(
                request.customerId(),
                show,
                idempotencyKey,
                total,
                BookingStatus.CONFIRMED
        );

        showSeats.forEach(showSeat ->
                booking.addSeat(new BookingSeat(showSeat.getSeat())));

        try {
            showSeatRepository.saveAllAndFlush(showSeats);
            Booking saved = bookingRepository.save(booking);
            return toResponse(saved);
        } catch (ObjectOptimisticLockingFailureException ex) {
            throw new IllegalStateException(
                    "Another customer booked one or more of the selected seats.");
        } catch (DataIntegrityViolationException ex) {
            // A unique constraint protects us if two identical idempotency keys race.
            return bookingRepository.findByIdempotencyKey(idempotencyKey)
                    .map(this::toResponse)
                    .orElseThrow(() -> ex);
        }
    }

    private BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getCustomerId(),
                booking.getShow().getId(),
                booking.getSeats().stream()
                        .map(bs -> bs.getSeat().getId())
                        .toList(),
                booking.getTotalAmount(),
                booking.getStatus().name(),
                booking.getCreatedAt()
        );
    }
}
