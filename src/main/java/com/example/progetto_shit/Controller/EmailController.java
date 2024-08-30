package com.example.progetto_shit.Controller;

import com.example.progetto_shit.Model.EmailObserver;
import com.example.progetto_shit.Model.MessageStorage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
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
        if (isReplyEmail(email)) {
            showReplyEmailDetailView(email);
        } else {
            String[] emailLines = email.split("\n", 4);
            String sender = emailLines.length > 0 ? emailLines[0].replace("From: ", "") : "Unknown Sender";
            String receiver = emailLines.length > 1 ? emailLines[1].replace("To: ", "") : "Unknown Receiver";
            String subject = emailLines.length > 2 ? emailLines[2].replace("Subject: ", "") : "No Subject";

            String fullEmailContent = MessageStorage.readReply(client, sender, subject);
            displayEmailContent(sender, receiver, subject, fullEmailContent);
        }
    }

    private void showReplyEmailDetailView(String email) {
        String[] emailLines = email.split("\n", 4);
        String sender = emailLines.length > 1 ? emailLines[1].replace("Reply from: ", "") : "Unknown Sender";
        String receiver = emailLines.length > 2 ? emailLines[2].replace("To: ", "") : "Unknown Receiver";
        String subject = emailLines.length > 3 ? emailLines[3].replace("Subject: ", "") : "No Subject";

        String fullEmailContent = MessageStorage.readReply(client, sender, subject);
        displayEmailContent(sender, receiver, subject, fullEmailContent);
    }

    private void displayEmailContent(String sender, String receiver, String subject, String fullEmailContent) {
        System.out.println("Full Email Content: " + fullEmailContent); // Debugging

        String[] emailParts = fullEmailContent.split("\n-----------------------------------\n");
        if (emailParts.length > 0) {
            String latestEmail = emailParts[emailParts.length - 1];
            String[] latestEmailLines = latestEmail.split("\n");
            StringBuilder bodyBuilder = new StringBuilder();
            boolean bodyStarted = false;

            for (String line : latestEmailLines) {
                if (bodyStarted) {
                    bodyBuilder.append(line).append("\n");
                } else if (line.startsWith("Body:")) {
                    bodyStarted = true;
                    // Rimuovi il prefisso "Body:" e aggiungi il resto della linea
                    bodyBuilder.append(line.substring(5).trim()).append("\n");
                }
            }

            String body = bodyBuilder.toString().trim();

            // Se il corpo è vuoto, potrebbe essere un'email senza il prefisso "Body:"
            if (body.isEmpty()) {
                body = String.join("\n", latestEmailLines);
            }

            if (emailParts.length > 1) {
                body += "\n\n----- Original Message -----\n" + String.join("\n", Arrays.copyOfRange(emailParts, 0, emailParts.length - 1));
            }

            // Rimuovi eventuali "No Body" alla fine del messaggio
            body = body.replaceAll("(?m)^No Body$", "").trim();

            Stage emailDetailStage = new Stage();
            emailDetailStage.setTitle("Email Details");

            Label senderLabel = new Label("From: " + sender);
            senderLabel.setPadding(new Insets(10));
            senderLabel.setStyle("-fx-font-weight: bold;");

            Label receiverLabel = new Label("To: " + receiver);
            receiverLabel.setPadding(new Insets(10));

            Label subjectLabel = new Label("Subject: " + subject);
            subjectLabel.setPadding(new Insets(10));

            Label bodyLabel = new Label(body);
            bodyLabel.setWrapText(true);
            bodyLabel.setPadding(new Insets(10));
            bodyLabel.setStyle("-fx-background-color: #f0f0f0;");

            Button replyButton = new Button("Reply");
            Button replyAllButton = new Button("Reply All");
            Button forwardButton = new Button("Forward");
            Button deleteButton = new Button("Delete");

            VBox buttonsBox = new VBox(10, replyButton, replyAllButton, forwardButton, deleteButton);
            buttonsBox.setPadding(new Insets(10));

            replyButton.setOnAction(event -> handleReply(fullEmailContent, false));
            replyAllButton.setOnAction(event -> handleReply(fullEmailContent, true));
            forwardButton.setOnAction(event -> handleForward(fullEmailContent));
            deleteButton.setOnAction(event -> handleDelete(fullEmailContent));

            VBox detailBox = new VBox(10, senderLabel, receiverLabel, subjectLabel, bodyLabel, buttonsBox);
            detailBox.setPadding(new Insets(15));

            ScrollPane scrollPane = new ScrollPane(detailBox);
            scrollPane.setFitToWidth(true);

            emailDetailStage.setScene(new Scene(scrollPane, 500, 400));
            emailDetailStage.show();
        } else {
            showAlert("Error", "Email content could not be parsed.");
        }
    }

    private boolean isReplyEmail(String email) {
        return email.contains("Reply from:");
    }

    private void handleReply(String email, boolean replyAll) {
        if (email != null) {
            String[] emailParts = email.split("\n-----------------------------------\n");
            String latestEmail = emailParts[emailParts.length - 1];

            String[] emailLines = latestEmail.split("\n", 4);
            String sender = emailLines.length > 0 ? emailLines[0].replace("From: ", "") : "Unknown Sender";
            String receiver = emailLines.length > 1 ? emailLines[1].replace("To: ", "") : "Unknown Receiver";
            String subject = emailLines.length > 2 ? emailLines[2].replace("Subject: ", "") : "No Subject";
            String body = emailLines.length > 3 ? emailLines[3].replace("Body: ", "") : "No Body";

            if (emailParts.length > 1) {
                body += "\n\n----- Original Message -----\n" + String.join("\n", Arrays.copyOfRange(emailParts, 0, emailParts.length - 1));
            }

            List<String> recipients = Arrays.asList(receiver.split(", "));

            ReplyHandler replyHandler = new ReplyHandler(client, sender, subject, body, recipients);
            replyHandler.replyToEmail(replyAll);
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
            String sender = emailLines.length > 0 ? emailLines[0].replace("From: ", "").trim() : null;
            String subject = emailLines.length > 1 ? emailLines[1].replace("Subject: ", "").trim() : null;

            System.out.println("Attempting to delete email:");
            System.out.println("Client: " + client);
            System.out.println("Sender: " + sender);
            System.out.println("Subject: " + subject);

            if (sender != null && subject != null) {
                // Recupera i destinatari dall'email originale per ricostruire il nome del file
                boolean success = MessageStorage.deleteMessage(client, sender, subject);
                System.out.println("Delete operation result: " + success);

                if (success) {
                    System.out.println("Email deleted successfully.");
                    loadEmails(); // Ricarica la lista delle email
                } else {
                    System.out.println("Failed to delete the email.");
                    showAlert("Error", "Failed to delete the email. Check console for more details.");
                }
            } else {
                System.out.println("Invalid email format. Sender or subject is null.");
                showAlert("Error", "Invalid email format.");
            }
        } else {
            System.out.println("No email selected for deletion.");
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
