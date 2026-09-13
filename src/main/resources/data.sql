-- =============================================================================
-- Seed data for the movie booking platform.
--
-- Runs automatically on every application startup (see application.yml,
-- spring.sql.init.mode=always), right after schema.sql recreates the tables -
-- so the app always starts from this exact, known dataset. This replaces the
-- old DataInitializer.java CommandLineRunner, which did the same thing in Java.
--
-- Deliberately includes edge cases worth testing, not just a happy path:
--   - Show 1  (12:00) and Show 3 (17:59) sit at the two INCLUDED edges of the
--     "afternoon" discount window (AfternoonDiscountStrategy checks getHour(),
--     so 17:59 -> hour 17 still counts as afternoon).
--   - Show 4  (18:00) is the immediately EXCLUDED edge - one hour later than
--     Show 3, no discount. Compare Show 3 vs Show 4 to test the boundary.
--   - Show 6  (09:00) and Show 5 (20:00) are ordinary non-afternoon shows.
--   - Show 7  is the SAME movie in a DIFFERENT city (Mumbai) on the SAME date,
--     to test that the city filter actually filters.
--   - Show 8  is the SAME movie/theatre on a DIFFERENT date, to test the date
--     filter.
--   - Show 9  is a MINI 2-seat show (non-numeric seat labels BAL1/BAL2), with
--     BAL1 already BOOKED - only one seat (BAL2) is left, and there physically
--     is no third seat to ever earn the third-ticket discount.
--   - Show 10 is FULLY SOLD OUT (all 10 seats BOOKED already), so a booking
--     attempt against it returns 409 immediately with no setup required.
--   - Show 11 is a DIFFERENT movie (movie_id 2 - the Hindi dub, same title
--     text as movie_id 1) in a third city (Bengaluru), to prove filtering is
--     by movieId and not by title text.
--   - Movie 3 ("The Silent Echo") intentionally has ZERO shows anywhere - a
--     valid movieId that should return an empty list, not an error.
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Movies
-- -----------------------------------------------------------------------------
INSERT INTO movies (id, title, language, genre) VALUES
    (1, 'Interstellar', 'English', 'Sci-Fi'),
    (2, 'Interstellar', 'Hindi', 'Sci-Fi'),   -- same title as movie 1, different language/id
    (3, 'The Silent Echo', 'English', 'Drama'); -- intentionally has no shows (edge case)

-- -----------------------------------------------------------------------------
-- Theatres
-- -----------------------------------------------------------------------------
INSERT INTO theatres (id, name, city) VALUES
    (1, 'XYZ Cinemas - Connaught Place', 'Delhi'),
    (2, 'XYZ Cinemas - Andheri', 'Mumbai'),
    (3, 'XYZ Cinemas - Indiranagar', 'Bengaluru');

-- -----------------------------------------------------------------------------
-- Seats (A1-A10 are the standard 10-seat hall; BAL1/BAL2 are a small,
-- non-numeric-labelled balcony used by the mini show below)
-- -----------------------------------------------------------------------------
INSERT INTO seats (id, seat_number) VALUES
    (1, 'A1'), (2, 'A2'), (3, 'A3'), (4, 'A4'), (5, 'A5'),
    (6, 'A6'), (7, 'A7'), (8, 'A8'), (9, 'A9'), (10, 'A10'),
    (11, 'BAL1'), (12, 'BAL2');

-- -----------------------------------------------------------------------------
-- Shows
-- -----------------------------------------------------------------------------
INSERT INTO shows (id, movie_id, theatre_id, show_date, show_time) VALUES
    (1,  1, 1, '2026-08-15', '12:00:00'), -- afternoon window: INCLUDED (start edge)
    (2,  1, 1, '2026-08-15', '15:00:00'), -- afternoon window: INCLUDED (mid) - primary documented example
    (3,  1, 1, '2026-08-15', '17:59:00'), -- afternoon window: INCLUDED (end edge, still hour 17)
    (4,  1, 1, '2026-08-15', '18:00:00'), -- afternoon window: EXCLUDED (one hour after show 3)
    (5,  1, 1, '2026-08-15', '20:00:00'), -- evening, no discount - primary documented example
    (6,  1, 1, '2026-08-15', '09:00:00'), -- morning, no discount
    (7,  1, 2, '2026-08-15', '16:00:00'), -- same movie/date, different city (Mumbai)
    (8,  1, 1, '2026-08-16', '15:00:00'), -- same movie/theatre, different date
    (9,  1, 1, '2026-08-15', '22:00:00'), -- mini 2-seat show, one seat pre-booked
    (10, 1, 1, '2026-08-15', '23:00:00'), -- fully sold-out show (all 10 seats booked)
    (11, 2, 3, '2026-08-15', '15:00:00'); -- Hindi edition (movie 2), Bengaluru

-- -----------------------------------------------------------------------------
-- Show seats: standard 10-seat inventory (all AVAILABLE) for the nine
-- "normal" shows above (1, 2, 3, 4, 5, 6, 7, 8, 11).
-- -----------------------------------------------------------------------------
INSERT INTO show_seats (show_id, seat_id, status, version) VALUES
    (1, 1, 'AVAILABLE', 0),
    (1, 2, 'AVAILABLE', 0),
    (1, 3, 'AVAILABLE', 0),
    (1, 4, 'AVAILABLE', 0),
    (1, 5, 'AVAILABLE', 0),
    (1, 6, 'AVAILABLE', 0),
    (1, 7, 'AVAILABLE', 0),
    (1, 8, 'AVAILABLE', 0),
    (1, 9, 'AVAILABLE', 0),
    (1, 10, 'AVAILABLE', 0),
    (2, 1, 'AVAILABLE', 0),
    (2, 2, 'AVAILABLE', 0),
    (2, 3, 'AVAILABLE', 0),
    (2, 4, 'AVAILABLE', 0),
    (2, 5, 'AVAILABLE', 0),
    (2, 6, 'AVAILABLE', 0),
    (2, 7, 'AVAILABLE', 0),
    (2, 8, 'AVAILABLE', 0),
    (2, 9, 'AVAILABLE', 0),
    (2, 10, 'AVAILABLE', 0),
    (3, 1, 'AVAILABLE', 0),
    (3, 2, 'AVAILABLE', 0),
    (3, 3, 'AVAILABLE', 0),
    (3, 4, 'AVAILABLE', 0),
    (3, 5, 'AVAILABLE', 0),
    (3, 6, 'AVAILABLE', 0),
    (3, 7, 'AVAILABLE', 0),
    (3, 8, 'AVAILABLE', 0),
    (3, 9, 'AVAILABLE', 0),
    (3, 10, 'AVAILABLE', 0),
    (4, 1, 'AVAILABLE', 0),
    (4, 2, 'AVAILABLE', 0),
    (4, 3, 'AVAILABLE', 0),
    (4, 4, 'AVAILABLE', 0),
    (4, 5, 'AVAILABLE', 0),
    (4, 6, 'AVAILABLE', 0),
    (4, 7, 'AVAILABLE', 0),
    (4, 8, 'AVAILABLE', 0),
    (4, 9, 'AVAILABLE', 0),
    (4, 10, 'AVAILABLE', 0),
    (5, 1, 'AVAILABLE', 0),
    (5, 2, 'AVAILABLE', 0),
    (5, 3, 'AVAILABLE', 0),
    (5, 4, 'AVAILABLE', 0),
    (5, 5, 'AVAILABLE', 0),
    (5, 6, 'AVAILABLE', 0),
    (5, 7, 'AVAILABLE', 0),
    (5, 8, 'AVAILABLE', 0),
    (5, 9, 'AVAILABLE', 0),
    (5, 10, 'AVAILABLE', 0),
    (6, 1, 'AVAILABLE', 0),
    (6, 2, 'AVAILABLE', 0),
    (6, 3, 'AVAILABLE', 0),
    (6, 4, 'AVAILABLE', 0),
    (6, 5, 'AVAILABLE', 0),
    (6, 6, 'AVAILABLE', 0),
    (6, 7, 'AVAILABLE', 0),
    (6, 8, 'AVAILABLE', 0),
    (6, 9, 'AVAILABLE', 0),
    (6, 10, 'AVAILABLE', 0),
    (7, 1, 'AVAILABLE', 0),
    (7, 2, 'AVAILABLE', 0),
    (7, 3, 'AVAILABLE', 0),
    (7, 4, 'AVAILABLE', 0),
    (7, 5, 'AVAILABLE', 0),
    (7, 6, 'AVAILABLE', 0),
    (7, 7, 'AVAILABLE', 0),
    (7, 8, 'AVAILABLE', 0),
    (7, 9, 'AVAILABLE', 0),
    (7, 10, 'AVAILABLE', 0),
    (8, 1, 'AVAILABLE', 0),
    (8, 2, 'AVAILABLE', 0),
    (8, 3, 'AVAILABLE', 0),
    (8, 4, 'AVAILABLE', 0),
    (8, 5, 'AVAILABLE', 0),
    (8, 6, 'AVAILABLE', 0),
    (8, 7, 'AVAILABLE', 0),
    (8, 8, 'AVAILABLE', 0),
    (8, 9, 'AVAILABLE', 0),
    (8, 10, 'AVAILABLE', 0),
    (11, 1, 'AVAILABLE', 0),
    (11, 2, 'AVAILABLE', 0),
    (11, 3, 'AVAILABLE', 0),
    (11, 4, 'AVAILABLE', 0),
    (11, 5, 'AVAILABLE', 0),
    (11, 6, 'AVAILABLE', 0),
    (11, 7, 'AVAILABLE', 0),
    (11, 8, 'AVAILABLE', 0),
    (11, 9, 'AVAILABLE', 0),
    (11, 10, 'AVAILABLE', 0);

-- -----------------------------------------------------------------------------
-- Show 9 (mini show): only two seats exist at all, one already booked -
-- exercises "no third seat exists to discount" and "only one seat left".
-- -----------------------------------------------------------------------------
INSERT INTO show_seats (show_id, seat_id, status, version) VALUES
    (9, 11, 'BOOKED', 0),    -- BAL1 - already taken
    (9, 12, 'AVAILABLE', 0); -- BAL2 - last seat remaining

-- -----------------------------------------------------------------------------
-- Show 10 (sold-out show): all ten seats already booked - a booking attempt
-- against any of these seats should fail with 409 immediately.
-- -----------------------------------------------------------------------------
INSERT INTO show_seats (show_id, seat_id, status, version) VALUES
    (10, 1, 'BOOKED', 0), (10, 2, 'BOOKED', 0), (10, 3, 'BOOKED', 0),
    (10, 4, 'BOOKED', 0), (10, 5, 'BOOKED', 0), (10, 6, 'BOOKED', 0),
    (10, 7, 'BOOKED', 0), (10, 8, 'BOOKED', 0), (10, 9, 'BOOKED', 0),
    (10, 10, 'BOOKED', 0);

-- -----------------------------------------------------------------------------
-- Bookings backing the pre-booked seats above, so the data is referentially
-- realistic (every BOOKED show_seat traces back to a real booking) and so
-- there's something to look at directly via BookingRepository/SQL too.
--
-- Pricing worked through for both, using the same rules as PricingService:
--   Booking 1 (show 10, 23:00, 10 seats): base 2000.00
--     - third-ticket discount: (2000 / 10) * 0.50 = 100.00
--     - afternoon discount: n/a (23:00 is not 12:00-17:59)
--     -> total 1900.00
--   Booking 2 (show 9, 22:00, 1 seat): base 200.00
--     - no third-ticket discount (only 1 ticket)
--     - afternoon discount: n/a (22:00 is not 12:00-17:59)
--     -> total 200.00
-- -----------------------------------------------------------------------------
INSERT INTO bookings (id, customer_id, show_id, idempotency_key, total_amount, status, created_at) VALUES
    (1, 999, 10, 'seed-fully-booked-demo-1', 1900.00, 'CONFIRMED', '2026-08-01 10:00:00.000000'),
    (2, 888, 9,  'seed-mini-show-preseat-1', 200.00,  'CONFIRMED', '2026-08-01 10:05:00.000000');

INSERT INTO booking_seats (booking_id, seat_id) VALUES
    (1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10),
    (2, 11);
