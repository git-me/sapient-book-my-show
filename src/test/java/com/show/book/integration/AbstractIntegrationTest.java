package com.show.book.integration;

import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * Common base for the read/write scenario integration tests.
 *
 * <p>The application connects to a local MySQL 8.0 instance for real usage
 * (see application.yml), and these tests boot the full Spring context against
 * a disposable, real MySQL 8.0 Testcontainers instance too - not an in-memory
 * substitute - so schema.sql/data.sql (which run on every startup, replacing
 * the old DataInitializer.java CommandLineRunner) are exercised exactly as
 * they would be against the real database engine.
 *
 * <p>Subclasses get a running Spring Boot application on a random port with
 * MockMvc wired in, and schema.sql + data.sql already applied by the time
 * each test method runs - see src/main/resources/data.sql for the exact
 * seeded movies/theatres/shows/seats/bookings (including the edge cases
 * referenced by ID throughout these tests).
 */
@Tag("integration")
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
abstract class AbstractIntegrationTest {

    @Container
    static final MySQLContainer<?> MYSQL =
            new MySQLContainer<>(DockerImageName.parse("mysql:8.0"));

    @DynamicPropertySource
    static void overrideDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
        registry.add("spring.datasource.driver-class-name", MYSQL::getDriverClassName);
    }
}
