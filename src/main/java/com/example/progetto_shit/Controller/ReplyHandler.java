package com.example.progetto_shit.Controller;

import com.example.progetto_shit.Model.MessageStorage;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ReplyHandler {

    private String senderAddress;
    private String clientAddress;
    private String originalSubject;
    private Runnable onReplySaved;
    private Stage primaryStage; // Reference to the primary stage

    public ReplyHandler(String senderAddress, String clientAddress, String originalSubject, Runnable onReplySaved) {
        this.senderAddress = senderAddress;
        this.clientAddress = clientAddress;
        this.originalSubject = originalSubject;
        this.onReplySaved = onReplySaved;
        this.primaryStage = primaryStage;
    }

    public void replyToEmail() {
        // Create a new stage for the reply dialog
        Stage replyStage = new Stage();
        replyStage.initModality(Modality.APPLICATION_MODAL);
        replyStage.initOwner(primaryStage); // Set the owner of the dialog

        replyStage.setTitle("Reply Email");

        // Create and configure the UI elements
        TextField subjectField = new TextField("RE: " + originalSubject);
        subjectField.setPromptText("Subject");

        TextArea bodyArea = new TextArea();
        bodyArea.setPromptText("Reply Body");
        bodyArea.setPrefRowCount(5);

        Button sendButton = new Button("Send");
        Button cancelButton = new Button("Cancel");

        VBox vbox = new VBox(10,
                new Label("To: " + senderAddress),
                new Label("Subject:"), subjectField,
                new Label("Body:"), bodyArea,
                sendButton, cancelButton);

        // Configure the send button action
        sendButton.setOnAction(e -> {
            String subject = subjectField.getText();
            String body = bodyArea.getText();

            if (body.isEmpty()) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("Reply Body Missing");
                errorAlert.setContentText("Please enter a reply body.");
                errorAlert.showAndWait();
                return;
            }

            // Save the message
            MessageStorage.saveMessage(
                    clientAddress, // The sender of the reply (original recipient)
                    senderAddress, // The recipient of the reply (original sender)
                    subject, // Subject for the reply
                    body, // The reply content
                    true // Indicates this is a reply
            );

            // Notify the controller to update the UI
            if (onReplySaved != null) {
                onReplySaved.run();
            }

            // Close the dialog
            replyStage.close();

            // Show confirmation alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Reply Sent");
            alert.setHeaderText(null);
            alert.setContentText("Reply sent to: " + senderAddress + "\nSubject: " + subject + "\nBody: " + body);
            alert.showAndWait();
        });

        // Configure the cancel button action
        cancelButton.setOnAction(e -> replyStage.close());

        // Set up the scene and stage
        Scene scene = new Scene(vbox, 400, 300);
        replyStage.setScene(scene);
        replyStage.showAndWait();
    }
}
