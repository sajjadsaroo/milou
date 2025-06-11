package com.milou.models;

import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "users")

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Email> sentEmails = new ArrayList<>();

    @ManyToMany(mappedBy = "recipients", fetch = FetchType.LAZY)
    private List<Email> receivedEmails = new ArrayList<>();

    // Constructors, Getters, Setters...
}