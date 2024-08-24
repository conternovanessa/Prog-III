package com.example.progetto_shit.Controller;

import com.example.progetto_shit.Main.Server;
import com.example.progetto_shit.Model.EmailObserver;
import com.example.progetto_shit.Model.MessageStorage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    private TextArea emailContentArea;

    @FXML
    private Label serverAddressLabel;

    @FXML
    private Button newMailButton;

    @FXML
    private Button receivedMailsButton;

    @FXML
    private Button backButton;

    @FXML
    private ScrollPane emailScrollPane;

    @FXML
    private VBox emailBox;

    private List<String> clientList = new ArrayList<>();
    private String selectedClient;
    private String selectedEmail;
    private String serverAddress;

    private static final String FILE_PATH = "src/main/java/com/example/progetto_shit/email.txt";

    @FXML
    public void initialize() {
        if (serverAddress != null) {
            serverAddressLabel.setText("Server Address: " + serverAddress);
        }

        if (selectedClient != null) {
            updateClientInterface();  // Mostra la lista delle email per il client selezionato
        } else {
            loadClientsFromFile(FILE_PATH);  // Mostra la lista dei clienti
        }
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
        if (serverAddressLabel != null) {
            serverAddressLabel.setText("Server Address: " + serverAddress);
        }
    }

    @FXML
    private void handleBack() {
        Platform.runLater(() -> {
            // Chiude la finestra del client
            Stage clientStage = (Stage) backButton.getScene().getWindow();
            clientStage.close();

            // Riapre la finestra del server
            Stage serverStage = new Stage();
            Server server = new Server();
            server.start(serverStage);  // Rilancia il server
        });
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
            String object = emailLines.length > 1 ? emailLines[1].replace("Subject: ", "") : "Unknown Subject";

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

                emailBox.getChildren().add(clientButton);
            }
        } catch (IOException e) {
            System.err.println("Error reading the specified file: " + e.getMessage());
        }
    }

    private void updateClientInterface() {
        emailBox.getChildren().clear();

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

        emailScrollPane.setContent(emailBox);
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
        emailDetailStage.setScene(new Scene(detailBox, 400, 300));
        emailDetailStage.show();
    }

    private List<String> getReceivedMailsForClient(String client) {
        return MessageStorage.getMessagesForRecipient(client);
    }

    private void updateEmailList(List<String> emails) {
        emailBox.getChildren().clear();
        for (String email : emails) {
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
