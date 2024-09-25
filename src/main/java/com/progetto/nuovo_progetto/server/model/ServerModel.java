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

    public ServerModel() {
        this.emailStore = new HashMap<>();
        this.emailFileManager = EmailFileManager.getInstance();
        this.logEntries = FXCollections.observableArrayList();
    }

    // Metodo per ottenere le email di un determinato indirizzo
    public List<Map<String, Object>> getEmails(String emailAddress) {
        return emailStore.getOrDefault(emailAddress, new ArrayList<>());
    }

    // Metodo per aggiungere un'email e salvarla su file
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
            // Passa i dettagli dell'email invece dell'oggetto Email
            emailFileManager.saveEmail(sender, recipients, subject, content, sentDate, isRead);
        } catch (IOException e) {
            e.printStackTrace();
            addLogEntry("Error saving email: " + e.getMessage());
        }
    }

    // Metodo per segnare tutte le email di un determinato indirizzo come lette
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

    // Metodo per ottenere i log
    public ObservableList<String> getLogEntries() {
        return logEntries;
    }

    // Metodo per aggiungere una voce di log
    public void addLogEntry(String entry) {
        logEntries.add(entry);
    }
}
