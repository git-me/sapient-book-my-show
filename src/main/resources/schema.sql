-- =============================================================================
-- Schema for the movie booking platform - MySQL 8.0.
--
-- This file (together with data.sql) is executed automatically on every
-- application startup because application.yml sets spring.sql.init.mode=always
-- and spring.jpa.hibernate.ddl-auto=none. Hibernate does not create, alter or
-- validate the schema at all - this script is the single source of truth for
-- it, and JPA entities/repositories (com.xyz.booking.domain / .repository) map
-- onto exactly what's created here.
--
-- Every table is dropped and recreated on each startup, so restarting the app
-- always gives you a clean, known dataset (see data.sql) - there is no manual
-- migration story here, by design, for this exercise.
-- =============================================================================

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS booking_seats;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS show_seats;
DROP TABLE IF EXISTS shows;
DROP TABLE IF EXISTS seats;
DROP TABLE IF EXISTS theatres;
DROP TABLE IF EXISTS movies;

CREATE TABLE movies (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    title    VARCHAR(255) NOT NULL,
    language VARCHAR(255),
    genre    VARCHAR(255)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE theatres (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE seats (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    seat_number VARCHAR(255) NOT NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE shows (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    movie_id   BIGINT NOT NULL,
    theatre_id BIGINT NOT NULL,
    show_date  DATE   NOT NULL,
    show_time  TIME   NOT NULL,
    CONSTRAINT fk_show_movie FOREIGN KEY (movie_id) REFERENCES movies (id),
    CONSTRAINT fk_show_theatre FOREIGN KEY (theatre_id) REFERENCES theatres (id),
    INDEX idx_show_movie_date (movie_id, show_date),
    INDEX idx_show_theatre_date (theatre_id, show_date)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE show_seats (
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    show_id BIGINT      NOT NULL,
    seat_id BIGINT      NOT NULL,
    status  VARCHAR(20) NOT NULL,
    version BIGINT      NOT NULL DEFAULT 0,
    CONSTRAINT uk_show_seat UNIQUE (show_id, seat_id),
    CONSTRAINT fk_showseat_show FOREIGN KEY (show_id) REFERENCES shows (id),
    CONSTRAINT fk_showseat_seat FOREIGN KEY (seat_id) REFERENCES seats (id),
    INDEX idx_show_seat_status (show_id, status)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE bookings (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id     BIGINT         NOT NULL,
    show_id         BIGINT         NOT NULL,
    idempotency_key VARCHAR(255)   NOT NULL,
    total_amount    DECIMAL(12, 2) NOT NULL,
    status          VARCHAR(20)    NOT NULL,
    created_at      DATETIME(6)    NOT NULL,
    CONSTRAINT uk_booking_idempotency UNIQUE (idempotency_key),
    CONSTRAINT fk_booking_show FOREIGN KEY (show_id) REFERENCES shows (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE booking_seats (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    seat_id    BIGINT NOT NULL,
    CONSTRAINT fk_bookingseat_booking FOREIGN KEY (booking_id) REFERENCES bookings (id),
    CONSTRAINT fk_bookingseat_seat FOREIGN KEY (seat_id) REFERENCES seats (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;
