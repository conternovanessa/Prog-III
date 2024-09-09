package com.example.progetto.Controller;

import com.example.progetto.Main.EmailDetailApplication;
import com.example.progetto.Model.EmailClientManager;
import com.example.progetto.Model.EmailObserver;
import com.example.progetto.Model.MessageStorage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class EmailController implements EmailObserver {

    @FXML private Label clientLabel;
    @FXML private ScrollPane emailScrollPane;
    @FXML private VBox emailBox;
    private EmailClientManager clientManager;

    private String client;
    private Stage primaryStage;
    private ServerController serverController;

    public void initialize() {
        clientManager = new EmailClientManager("localhost", 55555);
        startMessageReceiver();
    }

    private void startMessageReceiver() {
        clientManager.receiveMessages();
    }


    public void setClient(String client) {
        this.client = client;
        updateClientLabel();
        loadEmails();
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void setServerController(ServerController serverController) {
        this.serverController = serverController;
        System.out.println("ServerController set in EmailController");
    }

    private void updateClientLabel() {
        Platform.runLater(() -> {
            if (clientLabel != null) {
                clientLabel.setText("Emails for: " + client);
            }
        });
    }

    @FXML
    private void handleBack() {
        Platform.runLater(() -> {
            if (primaryStage != null) {
                primaryStage.close();
            }
        });
    }

    @FXML
    private void handleNewMail() {
        NewMailHandler newMailHandler = new NewMailHandler(client);
        String newEmail = newMailHandler.createNewMail();
        if (newEmail != null && !newEmail.isEmpty()) {
            try {
                clientManager.sendMessageToServer(newEmail);
                System.out.println("New email sent to server: " + newEmail);
                loadEmails();
            } catch (IOException e) {
                System.err.println("Error sending email: " + e.getMessage());
                showAlert("Error", "Failed to send email.");
            }
        }
    }

    @FXML
    private void handleRefresh() {
        loadEmails();
    }

    private synchronized void loadEmails() {
        List<String> emails = MessageStorage.getMessagesForRecipient(client);

        Platform.runLater(() -> {
            emailBox.getChildren().clear();
            for (String email : emails) {
                addEmailButton(email);
            }
            emailScrollPane.setContent(emailBox);
        });
    }

    private void addEmailButton(String email) {
        String[] emailLines = email.split("\n", 5);
        if (emailLines.length >= 4) {
            String date = emailLines[0].replace("Date: ", "");
            String sender = emailLines[1].replace("From: ", "");
            String subject = emailLines[3].replace("Subject: ", "");
            String buttonText = date + " - " + sender + " - " + subject;

            Button emailButton = new Button(buttonText);
            emailButton.setOnAction(event -> showEmailDetailView(email));

            boolean isRead = MessageStorage.isRead(email);
            updateButtonStyle(emailButton, isRead);

            emailBox.getChildren().add(emailButton);
        }
    }

    private void updateButtonStyle(Button button, boolean isRead) {
        if (isRead) {
            button.setStyle("-fx-background-color: lightgreen;");
        } else {
            button.setStyle("-fx-background-color: khaki; -fx-text-fill: black;");
        }
    }

    private synchronized void showEmailDetailView(String email) {
        Platform.runLater(() -> {
            String[] emailLines = email.split("\n", 5);
            if (emailLines.length >= 5) {
                String sender = emailLines[1].replace("From: ", "");
                String subject = emailLines[3].replace("Subject: ", "");

                boolean marked = MessageStorage.markAsRead(client, sender, subject);
                System.out.println("Email marked as read: " + marked);

                EmailDetailApplication detailApp = new EmailDetailApplication(email, client);
                try {
                    detailApp.start(new Stage());
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Error", "Failed to load email detail view.");
                }

                loadEmails();
            } else {
                System.out.println("Invalid email format. Lines: " + emailLines.length);
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void update(List<String> emails) {
        loadEmails();
    }
}