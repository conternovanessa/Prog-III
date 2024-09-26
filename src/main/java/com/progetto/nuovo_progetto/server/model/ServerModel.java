package com.progetto.nuovo_progetto.server.model;

import com.progetto.nuovo_progetto.common.EmailFileManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class ServerModel {
    private Map<String, List<Map<String, Object>>> emailStore;
    private EmailFileManager emailFileManager;
    private ObservableList<String> logEntries;
    private static final List<String> VALID_EMAILS = Arrays.asList(
            "filippoditto@progetto.com",
            "fabiodelia@progetto.com",
            "vanessaconterno@progetto.com"
    );

    public ServerModel() {
        this.emailStore = new HashMap<>();
        this.emailFileManager = EmailFileManager.getInstance();
        this.logEntries = FXCollections.observableArrayList();
    }

    public List<Map<String, Object>> getEmails(String emailAddress) {
        return emailStore.getOrDefault(emailAddress, new ArrayList<>());
    }

    public void addEmail(String recipient, String sender, List<String> recipients, String subject, String content, LocalDateTime sentDate, boolean isRead) {
        Map<String, Object> emailData = new HashMap<>();
        emailData.put("from", sender);
        emailData.put("to", recipients);
        emailData.put("subject", subject);
        emailData.put("content", content);
        emailData.put("date", sentDate);
        emailData.put("read", isRead);

        emailStore.computeIfAbsent(recipient, k -> new ArrayList<>()).add(emailData);

        try {
            emailFileManager.saveEmail(sender, recipients, subject, content, sentDate, isRead);
        } catch (IOException e) {
            e.printStackTrace();
            addLogEntry("Error saving email: " + e.getMessage());
        }
    }

    public void markEmailsAsRead(String emailAddress) {
        List<Map<String, Object>> emails = emailStore.get(emailAddress);
        if (emails != null) {
            for (Map<String, Object> email : emails) {
                email.put("read", true);
            }
            try {
                for (Map<String, Object> email : emails) {
                    String emailId = (String) email.get("id");
                    emailFileManager.updateEmailStatus(emailAddress, emailId, true);
                }
            } catch (IOException e) {
                e.printStackTrace();
                addLogEntry("Error marking emails as read: " + e.getMessage());
            }
        }
    }

    public ObservableList<String> getLogEntries() {
        return logEntries;
    }

    public void addLogEntry(String entry) {
        logEntries.add(entry);
    }

    // Nuovo metodo per verificare se un'email è valida
    public boolean isValidEmail(String email) {
        return VALID_EMAILS.contains(email);
    }
}
