package com.milou.models;
import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

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

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Email> sentEmails = new ArrayList<>();

    @ManyToMany(mappedBy = "recipients", fetch = FetchType.EAGER)
    private List<Email> receivedEmails = new ArrayList<>();

    public User(String name, String email, String password) {
        setName(name);
        setEmail(email);
        setPassword(password);
    }

    public User() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Email> getSentEmails() {
        return sentEmails;
    }

    public List<Email> getReceivedEmails() {
        return receivedEmails;
    }

    @Override
    public String toString() {
        return "User id : " + id + " name : " + name + " email : " + email + "\n";
    }

    public boolean equals(User user) {
        if(!Objects.equals(this.email, user.getEmail()))
            return false;
        if(!Objects.equals(this.name, user.getName()))
            return false;

        return true;
    }

}