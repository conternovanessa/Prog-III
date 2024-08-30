package com.example.progetto_shit.Controller;

import com.example.progetto_shit.Main.EmailDetailApplication;
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
        loadEmails();
    }

    private void loadEmails() {
        List<String> emails = MessageStorage.getMessagesForRecipient(client);

        Platform.runLater(() -> {
            emailBox.getChildren().clear();
            for (String email : emails) {
                String[] emailLines = email.split("\n", 4);
                if (emailLines.length >= 3) {
                    String sender = emailLines[0].replace("From: ", "");
                    String subject = emailLines[2].replace("Subject: ", "");
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
        Platform.runLater(() -> {
            EmailDetailApplication detailApp = new EmailDetailApplication(email, client);
            try {
                detailApp.start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to load email detail view.");
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