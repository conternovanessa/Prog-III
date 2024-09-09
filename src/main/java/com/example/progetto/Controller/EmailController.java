package com.example.progetto.Controller;

import com.example.progetto.Main.EmailDetailApplication;
import com.example.progetto.Model.EmailClientManager;
import com.example.progetto.Model.EmailObserver;
import com.example.progetto.Model.MessageStorage;
import com.example.progetto.Util.Logger;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.IOException;
import java.util.List;

public class EmailController implements EmailObserver {

    @FXML private Label clientLabel;
    @FXML private ScrollPane emailScrollPane;
    @FXML private VBox emailBox;
    private EmailClientManager clientManager;

    private Stage primaryStage;
    private ServerController serverController;

    private StringProperty clientProperty = new SimpleStringProperty();
    private ObservableList<String> emailList;

    public void initialize() {
        Logger.log("Initializing EmailController");
        clientManager = new EmailClientManager("localhost", 55555);
        startMessageReceiver();
    }

    private void startMessageReceiver() {
        Logger.log("Starting message receiver");
        clientManager.receiveMessages();
    }

    public void setClient(String client) {
        Logger.log("Setting client: " + client);
        this.clientProperty.set(client);
        updateClientLabel();
        loadEmails();
    }

    public String getClient() {
        return clientProperty.get();
    }

    public StringProperty clientProperty() {
        return clientProperty;
    }

    private void updateClientLabel() {
        Platform.runLater(() -> {
            if (clientLabel != null) {
                clientLabel.textProperty().bind(clientProperty);
            }
        });
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void setServerController(ServerController serverController) {
        this.serverController = serverController;
        Logger.log("ServerController set in EmailController");
    }

    @FXML
    private void handleBack() {
        Logger.log("Handling back action");
        Platform.runLater(() -> {
            if (primaryStage != null) {
                primaryStage.close();
            }
        });
    }

    @FXML
    private void handleNewMail() {
        Logger.log("Handling new mail for client: " + getClient());
        NewMailHandler newMailHandler = new NewMailHandler(getClient());
        String newEmail = newMailHandler.createNewMail();
        if (newEmail != null && !newEmail.isEmpty()) {
            try {
                clientManager.sendMessageToServer(newEmail);
                Logger.log("New email sent to server: " + newEmail);
                loadEmails();
            } catch (IOException e) {
                Logger.log("Error sending email: " + e.getMessage());
                showAlert("Error", "Failed to send email.");
            }
        }
    }

    @FXML
    private void handleRefresh() {
        Logger.log("Refreshing emails for client: " + getClient());
        loadEmails();
    }

    private void loadEmails() {
        Logger.log("Loading emails for client: " + getClient());
        emailList = MessageStorage.getMessagesForRecipient(getClient());
        emailList.addListener((ListChangeListener<String>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (String email : c.getAddedSubList()) {
                        Platform.runLater(() -> addEmailButton(email));
                    }
                }
            }
        });

        Platform.runLater(() -> {
            emailBox.getChildren().clear();
            for (String email : emailList) {
                addEmailButton(email);
            }
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
        Logger.log("Showing email detail view");
        Platform.runLater(() -> {
            String[] emailLines = email.split("\n", 5);
            if (emailLines.length >= 5) {
                String sender = emailLines[1].replace("From: ", "");
                String subject = emailLines[3].replace("Subject: ", "");

                boolean marked = MessageStorage.markAsRead(getClient(), sender, subject);
                Logger.log("Email marked as read: " + marked);

                EmailDetailApplication detailApp = new EmailDetailApplication(email, getClient());
                try {
                    detailApp.start(new Stage());
                } catch (Exception e) {
                    Logger.log("Failed to load email detail view: " + e.getMessage());
                    showAlert("Error", "Failed to load email detail view.");
                }

                loadEmails();
            } else {
                Logger.log("Invalid email format. Lines: " + emailLines.length);
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
        Platform.runLater(this::loadEmails);
    }
}
