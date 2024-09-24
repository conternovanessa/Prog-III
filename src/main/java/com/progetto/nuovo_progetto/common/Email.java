package com.progetto.nuovo_progetto.common;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Email implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String sender;
    private List<String> recipients;
    private String subject;
    private String body;
    private LocalDateTime sentDate;

    public Email() {
        this.id = UUID.randomUUID().toString();
        this.sentDate = LocalDateTime.now();
    }

    public Email(String sender, List<String> recipients, String subject, String body) {
        this();
        this.sender = sender;
        this.recipients = recipients;
        this.subject = subject;
        this.body = body;
    }

    public String getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
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

    public LocalDateTime getSentDate() {
        return sentDate;
    }

    public void setSentDate(LocalDateTime sentDate) {
        this.sentDate = sentDate;
    }

    @Override
    public String toString() {
        return "Email{" +
                "id='" + id + '\'' +
                ", sender='" + sender + '\'' +
                ", recipients=" + recipients +
                ", subject='" + subject + '\'' +
                ", sentDate=" + sentDate +
                '}';
    }
}
