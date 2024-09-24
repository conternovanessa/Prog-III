package com.progetto.nuovo_progetto.common;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Email implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final AtomicLong ID_GENERATOR = new AtomicLong();

    private long id;
    private String sender;
    private List<String> recipients;
    private String subject;
    private String content;
    private LocalDateTime sentDate;
    private boolean read;

    public Email() {
        this.id = ID_GENERATOR.incrementAndGet();
        this.sentDate = LocalDateTime.now();
        this.read = false;
    }

    public Email(String sender, List<String> recipients, String subject, String content) {
        this();
        this.sender = sender;
        this.recipients = recipients;
        this.subject = subject;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSentDate() {
        return sentDate;
    }

    public void setSentDate(LocalDateTime sentDate) {
        this.sentDate = sentDate;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    @Override
    public String toString() {
        return "Email{" +
                "id=" + id +
                ", sender='" + sender + '\'' +
                ", recipients=" + recipients +
                ", subject='" + subject + '\'' +
                ", sentDate=" + sentDate +
                ", read=" + read +
                '}';
    }
}

