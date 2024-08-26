package com.example.progetto_shit.Controller;

import com.example.progetto_shit.Model.EmailObserver;
import com.example.progetto_shit.Model.MessageStorage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class EmailController implements EmailObserver {

    @FXML
    private Label clientLabel;

    @FXML
    private Button newMailButton;

    @FXML
    private Button backButton;

    @FXML
    private Button refreshButton;

    @FXML
    private ScrollPane emailScrollPane;

    @FXML
    private VBox emailBox;

    private String client;
    private Stage primaryStage;

    public void setClient(String client) {
        this.client = client;
        if (clientLabel != null) {
            clientLabel.setText("Emails for: " + client);
        }
        loadEmails();
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    @FXML
    private void handleBack() {
        Platform.runLater(() -> {
            if (primaryStage != null) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/progetto_shit/View/client_view.fxml"));
                    Parent clientView = loader.load();
                    ClientController clientController = loader.getController();
                    clientController.setPrimaryStage(primaryStage);

                    Scene clientScene = new Scene(clientView);
                    primaryStage.setScene(clientScene);
                    primaryStage.setTitle("Client Selection");

                    primaryStage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert("Error", "Failed to load the client view.");
                }
            } else {
                showAlert("Error", "Primary stage is not set.");
            }
        });
    }

    @FXML
    private void handleNewMail() {
        NewMailHandler newMailHandler = new NewMailHandler(client);
        newMailHandler.createNewMail();
    }

    @FXML
    private void handleRefresh() {
        loadEmails(); // Ricarica le email dal MessageStorage
    }

    private void loadEmails() {
        List<String> emails = MessageStorage.getMessagesForRecipient(client);

        Platform.runLater(() -> {
            emailBox.getChildren().clear();
            for (String email : emails) {
                String[] emailLines = email.split("\n", 3);
                if (emailLines.length >= 2) {
                    String sender = emailLines[0].replace("From: ", "");
                    String subject = emailLines[1].replace("Subject: ", "");
                    String buttonText = sender + " - " + subject;

                    Button emailButton = new Button(buttonText);
                    emailButton.setOnAction(event -> showEmailDetailView(email));

                    emailBox.getChildren().add(emailButton);
                }
            }

            emailScrollPane.setContent(emailBox);
        });
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
        replyButton.setOnAction(event -> handleReply(email));
        Button forwardButton = new Button("Forward");
        forwardButton.setOnAction(event -> handleForward(email));

        // Aggiungi il pulsante "Elimina"
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(event -> handleDelete(email));

        VBox detailBox = new VBox(10, senderLabel, subjectLabel, bodyArea, replyButton, forwardButton, deleteButton);
        emailDetailStage.setScene(new Scene(detailBox, 400, 300));
        emailDetailStage.show();
    }

    private void handleReply(String email) {
        if (email != null) {
            String[] emailLines = email.split("\n", 3);
            String sender = emailLines.length > 0 ? emailLines[0].replace("From: ", "") : "Unknown Sender";
            String subject = emailLines.length > 1 ? emailLines[1].replace("Subject: ", "") : "Unknown Subject";

            ReplyHandler replyHandler = new ReplyHandler(sender, client, subject, this::loadEmails);
            replyHandler.replyToEmail();
        } else {
            showAlert("Selection Missing", "Please select an email to reply to.");
        }
    }

    private void handleForward(String email) {
        if (email != null) {
            ForwardHandler forwardHandler = new ForwardHandler(client);
            forwardHandler.forwardEmail(email);
        } else {
            showAlert("Selection Missing", "Please select an email to forward.");
        }
    }

    private void handleDelete(String email) {
        if (email != null) {
            String[] emailLines = email.split("\n", 3);
            String sender = emailLines.length > 0 ? emailLines[0].replace("From: ", "") : null;
            String subject = emailLines.length > 1 ? emailLines[1].replace("Subject: ", "") : null;

            if (sender != null && subject != null) {
                boolean success = MessageStorage.deleteMessage(client, sender, subject);
                if (success) {
                    loadEmails(); // Ricarica la lista delle email
                } else {
                    showAlert("Error", "Failed to delete the email.");
                }
            } else {
                showAlert("Error", "Invalid email format.");
            }
        } else {
            showAlert("Selection Missing", "Please select an email to delete.");
        }
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
