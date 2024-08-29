package com.example.progetto_shit.Controller;

import com.example.progetto_shit.Model.MessageStorage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;
public class ReplyHandler {
    private String clientAddress;
    private String originalSender;
    private String originalReceivers;
    private String originalSubject;
    private String originalBody;

    public ReplyHandler(String clientAddress, String originalSender, String originalSubject, String originalBody, List<String> recipients) {
        this(clientAddress, originalSender, "", originalSubject, originalBody);
    }

    public ReplyHandler(String clientAddress, String originalSender, String originalReceivers, String originalSubject, String originalBody) {
        this.clientAddress = clientAddress;
        this.originalSender = originalSender;
        this.originalReceivers = originalReceivers;
        this.originalSubject = originalSubject;
        this.originalBody = originalBody;
    }

    public void replyToEmail(boolean replyAll) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(replyAll ? "Reply All" : "Reply");

        TextField toField = new TextField();
        toField.setPromptText("To");
        toField.setText(replyAll ? originalSender + ", " + originalReceivers : originalSender);

        TextField subjectField = new TextField();
        subjectField.setPromptText("Subject");
        subjectField.setText("Re: " + originalSubject);

        TextArea bodyArea = new TextArea();
        bodyArea.setPromptText("Body");
        bodyArea.setPrefRowCount(10);
        bodyArea.setText("\n\n----- Original Message -----\n" + originalBody);

        VBox vbox = new VBox(10,
                new Label("To:"), toField,
                new Label("Subject:"), subjectField,
                new Label("Body:"), bodyArea);

        Button sendButton = new Button("Send");
        Button cancelButton = new Button("Cancel");

        sendButton.setOnAction(e -> {
            String to = toField.getText();
            String subject = subjectField.getText();
            String body = bodyArea.getText();

            if (to.isEmpty() || subject.isEmpty() || body.isEmpty()) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("Missing Details");
                errorAlert.setContentText("Please fill in all fields.");
                errorAlert.showAndWait();
                return;
            }

            try {
                List<String> recipients = Arrays.asList(to.split(",\\s*"));
                MessageStorage.saveMessage(
                        clientAddress,
                        recipients,
                        subject,
                        body,
                        true
                );

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Email Sent");
                alert.setHeaderText(null);
                alert.setContentText("Email sent from: " + clientAddress + "\nTo: " + to + "\nSubject: " + subject);
                alert.showAndWait();
                dialog.close();

            } catch (Exception ex) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("Save Error");
                errorAlert.setContentText("Could not save the reply. Error: " + ex.getMessage());
                errorAlert.showAndWait();
            }
        });

        cancelButton.setOnAction(e -> dialog.close());

        vbox.getChildren().addAll(sendButton, cancelButton);
        Scene dialogScene = new Scene(vbox, 400, 400);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }

}