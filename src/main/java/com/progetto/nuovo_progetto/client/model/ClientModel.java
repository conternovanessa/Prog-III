package com.progetto.nuovo_progetto.client.model;

import com.progetto.nuovo_progetto.common.Email;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ClientModel {
    private String emailAddress;
    private ObservableList<Email> inbox;
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 5000;

    public ClientModel(String emailAddress) {
        this.emailAddress = emailAddress;
        this.inbox = FXCollections.observableArrayList();
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public ObservableList<Email> getInbox() {
        return inbox;
    }

    public void addEmail(Email email) {
        Platform.runLater(() -> {
            inbox.add(email);
            inbox.sort(Comparator.comparing(Email::getSentDate).reversed());
        });
    }

    public void fetchEmails() {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject("FETCH_EMAILS");
            out.writeObject(emailAddress);
            out.flush();

            Object response = in.readObject();

            if (response instanceof ArrayList) {
                List<Map<String, Object>> emailDataList = (List<Map<String, Object>>) response;
                List<Email> emails = parseEmailDataList(emailDataList);

                Platform.runLater(() -> {
                    inbox.clear();
                    inbox.addAll(emails);
                    inbox.sort(Comparator.comparing(Email::getSentDate).reversed());
                });
            } else {
                throw new IOException("Unexpected response type from server: " + response.getClass().getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Qui potresti implementare una gestione degli errori più sofisticata
        }
    }

    private List<Email> parseEmailDataList(List<Map<String, Object>> emailDataList) {
        List<Email> emails = new ArrayList<>();
        for (Map<String, Object> emailData : emailDataList) {
            Email email = new Email();
            email.setId((String) emailData.get("id"));
            email.setSender((String) emailData.get("from"));

            Object toField = emailData.get("to");
            if (toField instanceof String) {
                email.setRecipients(Arrays.asList((String) toField));
            } else if (toField instanceof List) {
                email.setRecipients((List<String>) toField);
            } else {
                email.setRecipients(new ArrayList<>());
            }

            email.setSubject((String) emailData.get("subject"));
            email.setContent((String) emailData.getOrDefault("content", ""));

            Object dateField = emailData.get("date");
            if (dateField instanceof LocalDateTime) {
                email.setSentDate((LocalDateTime) dateField);
            } else if (dateField instanceof String) {
                email.setSentDate(LocalDateTime.parse((String) dateField));
            } else {
                email.setSentDate(LocalDateTime.now());
            }

            email.setRead((Boolean) emailData.get("read"));
            emails.add(email);
        }
        return emails;
    }

    public boolean updateEmailStatus(Email email) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject("UPDATE_EMAIL_STATUS");
            out.writeObject(emailAddress);
            out.writeObject(email.getId());
            out.writeBoolean(email.isRead());
            out.flush();

            String response = (String) in.readObject();
            return response.equals("SUCCESS");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendEmail(Email email) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject("SEND_EMAIL");
            out.writeObject(email);
            out.flush();

            String response = (String) in.readObject();
            return response.startsWith("SUCCESS");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void markAllEmailsAsRead() {
        for (Email email : inbox) {
            if (!email.isRead()) {
                email.setRead(true);
                updateEmailStatus(email);
            }
        }
    }

    public List<Email> getUnreadEmails() {
        List<Email> unreadEmails = new ArrayList<>();
        for (Email email : inbox) {
            if (!email.isRead()) {
                unreadEmails.add(email);
            }
        }
        return unreadEmails;
    }

    public boolean deleteEmail(Email email) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject("DELETE_EMAIL");
            out.writeObject(emailAddress);
            out.writeObject(email.getId());
            out.flush();

            String response = (String) in.readObject();
            if (response.equals("SUCCESS")) {
                Platform.runLater(() -> inbox.remove(email));
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}