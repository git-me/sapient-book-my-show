package com.show.book.domain;

import jakarta.persistence.*;

/**
 * Per-show seat inventory row. This - not {@link Seat} - is what booking
 * availability and the optimistic lock are checked against, since the same
 * physical seat is independently available or booked per show.
 */
@Entity
@Table(name = "show_seats",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_show_seat",
           columnNames = {"show_id", "seat_id"}
       ),
       indexes = @Index(name = "idx_show_seat_status", columnList = "show_id,status"))
public class ShowSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus status;

    @Version
    private long version;

    protected ShowSeat() {
    }

    public ShowSeat(Show show, Seat seat) {
        this.show = show;
        this.seat = seat;
        this.status = SeatStatus.AVAILABLE;
    }

    public Long getId() { return id; }
    public Show getShow() { return show; }
    public Seat getSeat() { return seat; }
    public SeatStatus getStatus() { return status; }

    public void book() {
        if (status != SeatStatus.AVAILABLE) {
            throw new IllegalStateException("Seat is already booked");
        }
        this.status = SeatStatus.BOOKED;
    }
}
