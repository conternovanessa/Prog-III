package com.example.progetto_shit.Controller;

import com.example.progetto_shit.Model.MessageStorage;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

public class ReplyHandler {

    private String senderAddress;
    private String clientAddress;
    private String originalSubject;
    private Runnable onReplySaved;

    public ReplyHandler(String senderAddress, String clientAddress, String originalSubject, Runnable onReplySaved) {
        this.senderAddress = senderAddress;
        this.clientAddress = clientAddress;
        this.originalSubject = originalSubject;
        this.onReplySaved = onReplySaved;
    }

    public void replyToEmail() {
        // Show a dialog to input the reply text
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reply Email");
        dialog.setHeaderText("Reply to: " + senderAddress);
        dialog.setContentText("Reply:");

        // Get the reply text from the user
        String reply = dialog.showAndWait().orElse("");

        if (!reply.isEmpty()) {
            // Simulate sending the reply
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Reply Email");
            alert.setHeaderText(null);
            alert.setContentText("Reply sent to: " + senderAddress + "\nContent: " + reply);
            alert.showAndWait();

            MessageStorage.saveMessage(
                    senderAddress, // The sender of the reply
                    clientAddress, // The recipient of the reply
                    "RE: " + originalSubject, // Subject for the reply
                    reply, // The reply content
                    true // Indicates this is a reply
            );

            // Notify the controller to update the UI
            if (onReplySaved != null) {
                onReplySaved.run();
            }
        }
    }
}
