package com.example.progetto_shit.Controller;

import com.example.progetto_shit.Model.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientController implements EmailObserver {

    @FXML
    private Label statusLabel;

    @FXML
    private VBox buttonBox;

    @FXML
    private TextArea emailContentArea;

    @FXML
    private Label serverAddressLabel;

    private List<String> clientList = new ArrayList<>();
    private String selectedClient;
    private String selectedEmail;
    private ServerController serverController;
    private String serverAddress;

    private static final String FILE_PATH = "src/main/java/com/example/progetto_shit/email.txt";

    @FXML
    public void initialize() {
        if (serverAddress != null) {
            serverAddressLabel.setText("Server Address: " + serverAddress);
        }
        loadClientsFromFile(FILE_PATH);
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
        if (serverAddressLabel != null) {
            serverAddressLabel.setText("Server Address: " + serverAddress);
        }
    }

    public void setServerController(ServerController serverController) {
        this.serverController = serverController;
    }

    @FXML
    private void handleBack() {
        System.out.println("Going back...");
        loadClientsFromFile(FILE_PATH);
    }

    @FXML
    private void handleNewMail() {
        System.out.println("Creating a new mail...");
        NewMailHandler newMailHandler = new NewMailHandler(selectedClient);
        newMailHandler.createNewMail();
    }

    @FXML
    private void handleReceivedMails() {
        System.out.println("Showing received emails...");
        updateClientInterface();
    }

    @FXML
    private void handleReply() {
        System.out.println("Replying to the email...");
        if (selectedEmail != null) {
            String[] emailLines = selectedEmail.split("\n", 3);
            String sender = emailLines.length > 0 ? emailLines[0].replace("From: ", "") : "Unknown Sender";
            String object = emailLines.length > 0 ? emailLines[0].replace("Subject: ", "") : "Unknown Sender";

            ReplyHandler replyHandler = new ReplyHandler(sender, selectedClient, object);
            replyHandler.replyToEmail();
        } else {
            showAlert("Selection Missing", "Please select an email to reply to.");
        }
    }

    @FXML
    private void handleForward() {
        System.out.println("Forwarding the email...");
        if (selectedEmail != null) {
            ForwardHandler forwardHandler = new ForwardHandler(selectedClient);
            forwardHandler.forwardEmail(selectedEmail);
        } else {
            showAlert("Selection Missing", "Please select an email to forward.");
        }
    }

    @FXML
    private void handleRefreshEmails() {
        System.out.println("Refreshing emails...");
        List<String> receivedMails = getReceivedMailsForClient(selectedClient);
        if (!receivedMails.isEmpty()) {
            showEmailDetailView(receivedMails.get(0));
        }
        updateEmailList(receivedMails);
    }

    private void loadClientsFromFile(String filePath) {
        buttonBox.getChildren().clear();
        Button stopButton = new Button("Stop Server");
        buttonBox.getChildren().addAll(statusLabel, stopButton);

        stopButton.setOnAction(event -> ServerController.stopServer(statusLabel));

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

        Button replyButton = new Button("Reply");
        replyButton.setOnAction(event -> handleReply());
        Button forwardButton = new Button("Forward");
        forwardButton.setOnAction(event -> handleForward());

        VBox detailBox = new VBox(10, senderLabel, subjectLabel, bodyArea, replyButton, forwardButton);
        detailBox.setPrefSize(400, 300);

        Scene detailScene = new Scene(detailBox);
        emailDetailStage.setScene(detailScene);
        emailDetailStage.show();
    }

    private VBox createActionButtons() {
        Button newMailButton = new Button("New Mail");
        Button receivedMailsButton = new Button("Refresh");
        Button backButton = new Button("Back");

        newMailButton.setOnAction(event -> handleNewMail());
        receivedMailsButton.setOnAction(event -> handleReceivedMails());
        backButton.setOnAction(event -> handleBack());

        VBox buttonBox = new VBox(10);
        buttonBox.getChildren().addAll(newMailButton, receivedMailsButton, backButton);
        return buttonBox;
    }

    private List<String> getReceivedMailsForClient(String client) {
        return MessageStorage.getMessagesForRecipient(client);
    }

    private void updateEmailList(List<String> receivedMails) {
        VBox emailBox = new VBox(10);
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

        ScrollPane emailScrollPane = (ScrollPane) buttonBox.lookup(".scroll-pane");
        if (emailScrollPane == null) {
            emailScrollPane = new ScrollPane(emailBox);
            emailScrollPane.setFitToWidth(true);
            emailScrollPane.setPrefHeight(200);
            buttonBox.getChildren().add(emailScrollPane);
        } else {
            emailScrollPane.setContent(emailBox);
        }
    }

    @Override
    public void update(List<String> emails) {
        updateEmailList(emails);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
