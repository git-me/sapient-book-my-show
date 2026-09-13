package com.show.book.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * A confirmed (or cancelled) reservation of one or more seats for a
 * {@link Show}, keyed for idempotent retries by {@code idempotencyKey}.
 */
@Entity
@Table(name = "bookings",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_booking_idempotency",
           columnNames = "idempotency_key"
       ))
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @Column(name = "idempotency_key", nullable = false, unique = true)
    private String idempotencyKey;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingSeat> seats = new ArrayList<>();

    protected Booking() {
    }

    public Booking(Long customerId, Show show, String idempotencyKey,
                   BigDecimal totalAmount, BookingStatus status) {
        this.customerId = customerId;
        this.show = show;
        this.idempotencyKey = idempotencyKey;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public Long getCustomerId() { return customerId; }
    public Show getShow() { return show; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public BookingStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public List<BookingSeat> getSeats() { return seats; }

    public void addSeat(BookingSeat bookingSeat) {
        seats.add(bookingSeat);
        bookingSeat.assignBooking(this);
    }
}
