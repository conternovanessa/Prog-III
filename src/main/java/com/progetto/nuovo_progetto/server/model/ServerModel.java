package com.progetto.nuovo_progetto.server.model;

import com.progetto.nuovo_progetto.common.Email;
import com.progetto.nuovo_progetto.common.EmailFileManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.*;

public class ServerModel {
    private Map<String, List<Email>> emailStore;
    private EmailFileManager emailFileManager;
    private ObservableList<String> logEntries;

    public ServerModel() {
        this.emailStore = new HashMap<>();
        this.emailFileManager = EmailFileManager.getInstance();
        this.logEntries = FXCollections.observableArrayList();
    }

    public List<Email> getEmails(String emailAddress) {
        return emailStore.getOrDefault(emailAddress, new ArrayList<>());
    }

    public void addEmail(String recipient, Email email) {
        emailStore.computeIfAbsent(recipient, k -> new ArrayList<>()).add(email);
        try {
            emailFileManager.saveEmail(email);
        } catch (IOException e) {
            e.printStackTrace();
            addLogEntry("Error saving email: " + e.getMessage());
        }
    }

    public void markEmailsAsRead(String emailAddress) {
        List<Email> emails = emailStore.get(emailAddress);
        if (emails != null) {
            for (Email email : emails) {
                email.setRead(true);
            }
            try {
                for (Email email : emails) {
                    emailFileManager.updateEmailStatus(emailAddress, email.getId(), true);
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
}
