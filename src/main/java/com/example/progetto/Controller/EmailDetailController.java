package com.example.progetto.Controller;

import com.example.progetto.Model.MessageStorage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.util.*;

import java.util.logging.Logger;

public class EmailDetailController {

    @FXML
    private Label senderLabel;

    @FXML
    private Label receiverLabel;

    @FXML
    private Label subjectLabel;

    @FXML
    private TextArea bodyTextArea;

    private Stage stage;
    private String client;
    private String fullEmailContent;

    private static final Logger logger = Logger.getLogger(EmailDetailController.class.getName());

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
        // Dividi l'email in righe e estrai mittente, destinatari, oggetto e corpo
        String[] emailLines = fullEmailContent.split("\n", 5);
        String sender = emailLines.length > 1 ? emailLines[1].replace("From: ", "").trim().toLowerCase() : "unknown.sender@example.com";
        String receiver = emailLines.length > 2 ? emailLines[2].replace("To: ", "").trim().toLowerCase() : "unknown.receiver@example.com";
        String subject = emailLines.length > 3 ? emailLines[3].replace("Subject: ", "").trim() : "No Subject";
        String body = emailLines.length > 4 ? emailLines[4].replace("Body: ", "").trim() : "";

        // Utilizza un Set per evitare duplicati
        Set<String> recipients = new LinkedHashSet<>();

        if (replyAll) {
            // Suddividi i destinatari separati da virgola
            String[] allReceivers = receiver.split(",");
            for (String recipient : allReceivers) {
                String trimmedRecipient = recipient.trim().toLowerCase();
                // Aggiungi solo le email che non sono quella del mittente (client)
                if (!trimmedRecipient.equals(client)) {
                    recipients.add(trimmedRecipient);
                }
            }
        } else {
            // Per una semplice risposta, rispondi solo al mittente
            recipients.add(sender);
        }

        // Crea un ReplyHandler con i destinatari senza duplicati
        ReplyHandler replyHandler = new ReplyHandler(client, sender, subject, body, new ArrayList<>(recipients));
        replyHandler.replyToEmail(replyAll);
        logger.info(replyAll ? "Reply All sent" : "Reply sent" + client);
        stage.close();
    }

    @FXML
    private void handleForward() {
        ForwardHandler forwardHandler = new ForwardHandler(client);
        forwardHandler.forwardEmail(fullEmailContent);
        logger.info("Email forwarded " + client);
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
            logger.info("Email deleted successfully.");
        } else {
            logger.warning("Failed to delete the email." + client);
        }
        stage.close();
    }

    @FXML
    private void handleBack() {
        stage.close();
    }
}