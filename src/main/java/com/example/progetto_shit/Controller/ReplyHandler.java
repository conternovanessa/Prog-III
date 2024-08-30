package com.example.progetto_shit.Controller;

import com.example.progetto_shit.Model.MessageStorage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReplyHandler {
    private String clientAddress;
    private String originalSender;
    private List<String> originalReceivers;
    private String originalSubject;
    private String originalBody;

    public ReplyHandler(String clientAddress, String originalSender, String originalSubject, String originalBody, List<String> recipients) {
        this.clientAddress = clientAddress;
        this.originalSender = originalSender;
        this.originalReceivers = new ArrayList<>(recipients);
        this.originalSubject = originalSubject;
        this.originalBody = originalBody;
    }

    public void replyToEmail(boolean replyAll) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(replyAll ? "Reply All" : "Reply");

        TextField fromField = new TextField(clientAddress);
        fromField.setEditable(false);

        TextField toField = new TextField();
        toField.setPromptText("To");

        if (replyAll) {
            List<String> toList = new ArrayList<>();
            toList.add(originalSender);
            toList.addAll(originalReceivers.stream()
                    .filter(receiver -> !receiver.equals(clientAddress))
                    .collect(Collectors.toList()));
            toField.setText(String.join(", ", toList));
        } else {
            toField.setText(originalSender);
        }

        TextField subjectField = new TextField();
        subjectField.setPromptText("Subject");
        subjectField.setText("Re: " + originalSubject);

        TextArea bodyArea = new TextArea();
        bodyArea.setPromptText("Body");
        bodyArea.setPrefRowCount(10);
        bodyArea.setText("\n\n----- Original Message -----\n" + originalBody);

        VBox vbox = new VBox(10,
                new Label("From:"), fromField,
                new Label("To:"), toField,
                new Label("Subject:"), subjectField,
                new Label("Body:"), bodyArea);

        Button sendButton = new Button("Send");
        Button cancelButton = new Button("Cancel");

        sendButton.setOnAction(e -> {
            String from = fromField.getText();
            String to = toField.getText();
            String subject = subjectField.getText();
            String body = bodyArea.getText();

            if (to.isEmpty() || subject.isEmpty() || body.isEmpty()) {
                showErrorAlert("Missing Details", "Please fill in all fields.");
                return;
            }

            try {
                List<String> recipients = Arrays.asList(to.split(",\\s*"));
                MessageStorage.saveMessage(
                        from,
                        recipients,
                        subject,
                        body,
                        true
                );

                showInfoAlert("Email Sent", "Email sent from: " + from + "\nTo: " + to + "\nSubject: " + subject);
                dialog.close();

            } catch (Exception ex) {
                showErrorAlert("Save Error", "Could not save the reply. Error: " + ex.getMessage());
            }
        });

        cancelButton.setOnAction(e -> dialog.close());

        vbox.getChildren().addAll(sendButton, cancelButton);
        Scene dialogScene = new Scene(vbox, 400, 400);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }

    private void showErrorAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfoAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}