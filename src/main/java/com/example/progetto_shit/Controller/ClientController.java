package com.example.progetto_shit.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientController {

    @FXML
    private Label statusLabel;

    @FXML
    private VBox buttonBox;

    @FXML
    private TextArea emailContentArea;

    private List<String> clientList = new ArrayList<>();
    private String selectedClient;
    private String selectedEmail;
    private ServerController serverController;
    @FXML
    private Label serverAddressLabel;

    private String serverAddress;

    public void initialize() {
        // Questo metodo viene chiamato dopo che l'FXML è stato caricato
    }

    public void initialize(String serverAddress) {
        this.serverAddress = serverAddress;
        serverAddressLabel.setText("Server Address: " + serverAddress);
    }

    public void setServerController(ServerController serverController) {
        this.serverController = serverController;
    }

    public void connectToServer() {
        System.out.println("Connecting to server...");
    }

    public void updateUI(String data) {
        emailContentArea.setText(data);
    }

    @FXML
    private void handleSendEmail() {
        System.out.println("Sending email...");
    }

    @FXML
    private void handleRefreshEmails() {
        System.out.println("Refreshing emails...");
        updateClientInterface();
    }

    public void loadClientsFromFile(String filePath) {
        buttonBox.getChildren().clear();

        Button stopButton = new Button("Stop Server");
        buttonBox.getChildren().addAll(statusLabel, stopButton);

        stopButton.setOnAction(event -> {
            if (serverController != null) {
                serverController.stopServer(); // Usa l'istanza di ServerController
            }
        });

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String client = line.trim();
                clientList.add(client);

                Button clientButton = new Button("Connect to " + client);
                clientButton.setOnAction(event -> {
                    selectedClient = client;
                    updateClientInterface();
                });

                buttonBox.getChildren().add(clientButton);
            }

        } catch (IOException e) {
            System.err.println("Error reading the specified file: " + e.getMessage());
        }
    }

    private void updateClientInterface() {
        buttonBox.getChildren().clear();

        VBox emailBox = new VBox(10);
        List<String> receivedMails = getReceivedMailsForClient(selectedClient);
        for (String email : receivedMails) {
            String[] emailLines = email.split("\n", 3);

            if (emailLines.length >= 2) {
                String sender = emailLines[0].replace("From: ", "");
                String subject = emailLines[1].replace("Subject: ", "");
                String buttonText = sender + " - " + subject;

                Button emailButton = new Button(buttonText);
                emailButton.setOnAction(event -> {
                    selectedEmail = email;
                    showEmailDetailView(email);
                });

                emailBox.getChildren().add(emailButton);
            }
        }

        ScrollPane emailScrollPane = new ScrollPane(emailBox);
        emailScrollPane.setFitToWidth(true);
        emailScrollPane.setPrefHeight(200);

        VBox contentBox = new VBox(10);
        contentBox.getChildren().addAll(emailScrollPane, createActionButtons());

        buttonBox.getChildren().add(contentBox);
    }

    private List<String> getReceivedMailsForClient(String client) {
        List<String> emails = new ArrayList<>();
        emails.add("From: example1@example.com\nSubject: Hello World\nThis is a test email.");
        emails.add("From: example2@example.com\nSubject: Another Email\nHere is some more content.");
        return emails;
    }

    private void showEmailDetailView(String email) {
        String[] emailLines = email.split("\n", 3);
        String sender = emailLines.length > 0 ? emailLines[0].replace("From: ", "") : "Unknown Sender";
        String subject = emailLines.length > 1 ? emailLines[1].replace("Subject: ", "") : "No Subject";
        String body = emailLines.length > 2 ? emailLines[2] : "No Content";

        Stage emailDetailStage = new Stage();
        emailDetailStage.setTitle("Email Details");

        Label senderLabel = new Label("From: " + sender);
        Label subjectLabel = new Label("Subject: " + subject);
        TextArea bodyArea = new TextArea(body);
        bodyArea.setWrapText(true);
        bodyArea.setEditable(false);

        VBox emailDetailBox = new VBox(10);
        emailDetailBox.getChildren().addAll(senderLabel, subjectLabel, bodyArea);

        Scene scene = new Scene(emailDetailBox, 300, 400);
        emailDetailStage.setScene(scene);
        emailDetailStage.show();
    }

    private VBox createActionButtons() {
        Button replyButton = new Button("Reply");
        Button forwardButton = new Button("Forward");

        replyButton.setOnAction(event -> handleReply());
        forwardButton.setOnAction(event -> handleForward());

        VBox actionButtons = new VBox(10);
        actionButtons.getChildren().addAll(replyButton, forwardButton);

        return actionButtons;
    }

    @FXML
    private void handleReply() {
        if (selectedEmail == null) {
            showErrorAlert("No email selected", "Please select an email to reply to.");
            return;
        }
        System.out.println("Replying to email: " + selectedEmail);
    }

    @FXML
    private void handleForward() {
        if (selectedEmail == null) {
            showErrorAlert("No email selected", "Please select an email to forward.");
            return;
        }
        System.out.println("Forwarding email: " + selectedEmail);
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleBack(ActionEvent actionEvent) {
    }
}
