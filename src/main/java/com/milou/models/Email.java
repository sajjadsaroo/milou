package com.milou.models;

import com.milou.dao.UserDao;
import com.milou.services.AuthService;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

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

    public String getFrom() {
        return this.sender != null ? this.sender.getEmail() : "[unknown]";
    }

    public String getFormattedTimestamp() {
        if (this.timestamp == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return this.timestamp.format(formatter);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Code: ").append(this.getMessage_code()).append("\n");

        sb.append("Recipient(s): ");
        if (this.recipients != null && !this.recipients.isEmpty()) {
            String recipientsString = this.recipients.stream()
                    .map(User::getEmail)
                    .collect(Collectors.joining(", "));
            sb.append(recipientsString);
        }
        sb.append("\n");

        sb.append("Subject: ").append(this.getSubject()).append("\n");

        if (this.timestamp != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            sb.append("Date: ").append(this.timestamp.format(formatter)).append("\n");
        }

        sb.append("\n");
        sb.append(this.getBody());

        return sb.toString();
    }
}