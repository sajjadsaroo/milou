package com.milou.models;

import com.milou.dao.UserDao;
import com.milou.services.AuthService;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "emails")
public class Email {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String message_code;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "email_recipient",
            joinColumns = @JoinColumn(name = "email_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> recipients = new ArrayList<>();

    private String subject;


    @Lob
    private String body;

    private LocalDateTime timestamp;

    public Email(User sender, String subject, String body) {
        this.sender = sender;
        this.subject = subject;
        this.body = body;
    }

    public Email() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage_code() {
        return message_code;
    }

    public void setMessage_code(String messageCode) {
        this.message_code = messageCode;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public List<User> getRecipients() {
        return recipients;
    }

    public void addRecipients(String recipient) {

        AuthService authService = new AuthService();
        recipient = authService.normalizeEmail(recipient);


        UserDao userDao = new UserDao();
        User rec = userDao.findByEmail(recipient);

        if (rec == null) {
            System.out.println(recipient + " does not exist!");
            return;
        }

        recipients.add(rec);
    }

    public void setRecipients(List<User> recipients) {
        this.recipients = recipients;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Email id" + getId() + "Subject: " + getSubject() + "Body: " + getBody() + "Timestamp: " + getTimestamp();
    }
}