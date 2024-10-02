package com.progetto.nuovo_progetto.server.model;

import com.progetto.nuovo_progetto.common.Email;
import com.progetto.nuovo_progetto.common.EmailFileManager;
import javafx.application.Platform;
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
        loadExistingEmails();
    }

    private void loadExistingEmails() {
        for (String emailAddress : VALID_EMAILS) {
            try {
                List<Map<String, Object>> emails = emailFileManager.getEmailSummaries(emailAddress);
                emailStore.put(emailAddress, emails);
                addLogEntry("Loaded " + emails.size() + " emails for " + emailAddress);
            } catch (IOException e) {
                e.printStackTrace();
                addLogEntry("Error loading emails for " + emailAddress + ": " + e.getMessage());
            }
        }
    }

    public List<Map<String, Object>> getEmails(String emailAddress) {
        return emailStore.getOrDefault(emailAddress, new ArrayList<>());
    }

    public void addEmail(Email email) {
        try {
            emailFileManager.saveEmail(email);
            // Aggiungi l'email alla struttura dati in memoria, se necessario
        } catch (IOException e) {
            e.printStackTrace();
            // Gestisci l'errore appropriatamente
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
        Platform.runLater(() -> logEntries.add(entry));
    }

    public boolean isValidEmail(String email) {
        return VALID_EMAILS.contains(email);
    }
}
