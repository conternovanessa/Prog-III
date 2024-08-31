package com.example.progetto.Controller;

import com.example.progetto.Model.MessageStorage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

public class EmailDetailController {

    @FXML
    private Label senderLabel;

    @FXML
    private Label receiverLabel;

    @FXML
    private Label subjectLabel;

    @FXML
    private TextArea bodyTextArea;

    @FXML
    private Button replyButton;

    @FXML
    private Button replyAllButton;

    @FXML
    private Button forwardButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button backButton;

    private Stage stage;
    private String client;
    private String fullEmailContent;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public void setEmailContent(String email) {
        this.fullEmailContent = email;
        String[] emailLines = email.split("\n", 5);
        // Skip the date line (index 0)
        String sender = emailLines.length > 1 ? emailLines[1].replace("From: ", "") : "Unknown Sender";
        String receiver = emailLines.length > 2 ? emailLines[2].replace("To: ", "") : "Unknown Receiver";
        String subject = emailLines.length > 3 ? emailLines[3].replace("Subject: ", "") : "No Subject";
        String body = emailLines.length > 4 ? emailLines[4].replace("Body: ", "") : "";

        senderLabel.setText("From: " + sender);
        receiverLabel.setText("To: " + receiver);
        subjectLabel.setText("Subject: " + subject);
        bodyTextArea.setText(body);
    }

    @FXML
    private void handleReply() {
        handleReplyOrReplyAll(false);
    }

    @FXML
    private void handleReplyAll() {
        handleReplyOrReplyAll(true);
    }

    private void handleReplyOrReplyAll(boolean replyAll) {
        String[] emailLines = fullEmailContent.split("\n", 5);
        // Skip the date line (index 0)
        String sender = emailLines.length > 1 ? emailLines[1].replace("From: ", "") : "Unknown Sender";
        String receiver = emailLines.length > 2 ? emailLines[2].replace("To: ", "") : "Unknown Receiver";
        String subject = emailLines.length > 3 ? emailLines[3].replace("Subject: ", "") : "No Subject";
        String body = emailLines.length > 4 ? emailLines[4].replace("Body: ", "") : "";

        List<String> recipients = Arrays.asList(receiver.split(", "));

        ReplyHandler replyHandler = new ReplyHandler(client, sender, subject, body, recipients);
        replyHandler.replyToEmail(replyAll);
        stage.close();
    }

    @FXML
    private void handleForward() {
        ForwardHandler forwardHandler = new ForwardHandler(client);
        forwardHandler.forwardEmail(fullEmailContent);
        stage.close();
    }

    @FXML
    private void handleDelete() {
        String[] emailLines = fullEmailContent.split("\n", 5);
        // Skip the date line (index 0)
        String sender = emailLines.length > 1 ? emailLines[1].replace("From: ", "") : "Unknown Sender";
        String subject = emailLines.length > 3 ? emailLines[3].replace("Subject: ", "") : "No Subject";

        boolean success = MessageStorage.deleteMessage(client, sender, subject);
        if (success) {
            System.out.println("Email deleted successfully.");
        } else {
            System.out.println("Failed to delete the email.");
        }
        stage.close();
    }

    @FXML
    private void handleBack() {
        stage.close();
    }
}
