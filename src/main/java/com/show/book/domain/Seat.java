package com.show.book.domain;

import jakarta.persistence.*;

/**
 * A physical seat identifier (e.g. "A1"), shared across every show it is
 * scheduled for. Its live availability for a specific show lives on
 * {@link ShowSeat}, not here.
 */
@Entity
@Table(name = "seats")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String seatNumber;

    protected Seat() {
    }

    public Seat(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public Long getId() { return id; }
    public String getSeatNumber() { return seatNumber; }
}
