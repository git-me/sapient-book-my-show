package com.show.book.domain;

import jakarta.persistence.*;

/**
 * A theatre (B2B partner) hosting shows in a given city.
 */
@Entity
@Table(name = "theatres")
public class Theatre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String city;

    protected Theatre() {
    }

    public Theatre(String name, String city) {
        this.name = name;
        this.city = city;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCity() { return city; }
}
