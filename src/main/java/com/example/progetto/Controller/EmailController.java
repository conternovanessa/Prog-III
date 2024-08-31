package com.example.progetto.Controller;

import com.example.progetto.Main.EmailDetailApplication;
import com.example.progetto.Model.EmailObserver;
import com.example.progetto.Model.MessageStorage;
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
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/progetto/View/client_view.fxml"));
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
        loadEmails();
    }

    private void loadEmails() {
        List<String> emails = MessageStorage.getMessagesForRecipient(client);

        Platform.runLater(() -> {
            emailBox.getChildren().clear();
            for (String email : emails) {
                String[] emailLines = email.split("\n", 5);
                if (emailLines.length >= 4) {
                    String date = emailLines[0].replace("Date: ", "");
                    String sender = emailLines[1].replace("From: ", "");
                    String subject = emailLines[3].replace("Subject: ", "");
                    String buttonText = date + " - " + sender + " - " + subject;

                    Button emailButton = new Button(buttonText);
                    emailButton.setOnAction(event -> showEmailDetailView(email));

                    boolean isRead = MessageStorage.isRead(email);
                    System.out.println("Email read status: " + isRead);
                    System.out.println("Button text: " + buttonText);

                    if (isRead) {
                        emailButton.setStyle("-fx-background-color: lightgreen;");
                        System.out.println("Setting button color to lightgray");
                    } else {
                        emailButton.setStyle("-fx-background-color: khaki ; -fx-text-fill: black;");
                        System.out.println("Setting button color to lightblue");
                    }

                    emailBox.getChildren().add(emailButton);
                }
            }

            emailScrollPane.setContent(emailBox);
        });
    }


    private void showEmailDetailView(String email) {
        Platform.runLater(() -> {
            String[] emailLines = email.split("\n", 5);  // Aumentato a 5 per includere la riga "Body:"
            if (emailLines.length >= 5) {
                String date = emailLines[0].replace("Date: ", "");
                String sender = emailLines[1].replace("From: ", "");
                String recipient = emailLines[2].replace("To: ", "");
                String subject = emailLines[3].replace("Subject: ", "");

                System.out.println("Marking email as read:");
                System.out.println("Date: " + date);
                System.out.println("From: " + sender);
                System.out.println("To: " + recipient);
                System.out.println("Subject: " + subject);

                // Mark the email as read
                boolean marked = MessageStorage.markAsRead(client, sender, subject);
                System.out.println("Email marked as read: " + marked);

                EmailDetailApplication detailApp = new EmailDetailApplication(email, client);
                try {
                    detailApp.start(new Stage());
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Error", "Failed to load email detail view.");
                }

                // Reload emails to update the UI
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