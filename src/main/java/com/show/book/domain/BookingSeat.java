package com.show.book.domain;

import jakarta.persistence.*;

/**
 * Join row recording which physical seats belong to a {@link Booking}.
 */
@Entity
@Table(name = "booking_seats")
public class BookingSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    protected BookingSeat() {
    }

    public BookingSeat(Seat seat) {
        this.seat = seat;
    }

    void assignBooking(Booking booking) {
        this.booking = booking;
    }

    public Seat getSeat() { return seat; }
}
