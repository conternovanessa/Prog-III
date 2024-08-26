package com.example.progetto_shit.Controller;

import com.example.progetto_shit.Model.MessageStorage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Collections;

public class ReplyHandler {

    private String clientAddress; // Email del client che risponde
    private String originalSender; // Email del mittente dell'email originale
    private String originalSubject; // Oggetto dell'email originale
    private String originalBody; // Corpo dell'email originale

    public ReplyHandler(String clientAddress, String originalSender, String originalSubject, String originalBody) {
        this.clientAddress = clientAddress;
        this.originalSender = originalSender;
        this.originalSubject = originalSubject;
        this.originalBody = originalBody;
    }

    public void replyToEmail() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Rispondi Email");

        TextField subjectField = new TextField();
        subjectField.setPromptText("Oggetto");
        subjectField.setText("Re: " + originalSubject);

        TextArea bodyArea = new TextArea();
        bodyArea.setPromptText("Corpo");
        bodyArea.setPrefRowCount(5);
        bodyArea.setText("=== Messaggio Originale ===\n" + originalBody + "\n\n");

        VBox vbox = new VBox(10,
                new javafx.scene.control.Label("Oggetto:"), subjectField,
                new javafx.scene.control.Label("Corpo:"), bodyArea);

        Button sendButton = new Button("Invia");
        Button cancelButton = new Button("Annulla");

        sendButton.setOnAction(e -> {
            String subject = subjectField.getText();
            String body = bodyArea.getText();

            if (subject.isEmpty() || body.isEmpty()) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Errore");
                errorAlert.setHeaderText("Dettagli mancanti");
                errorAlert.setContentText("Per favore, compila tutti i campi.");
                errorAlert.showAndWait();
                return;
            }

            // Salva la risposta invertendo mittente e destinatario
            try {
                MessageStorage.saveMessage(
                        clientAddress, // Mittente della risposta
                        Collections.singletonList(originalSender), // Il destinatario è il mittente originale
                        subject, // Oggetto della risposta
                        body, // Corpo della risposta
                        true // Indica che è una risposta
                );

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Risposta Email");
                alert.setHeaderText(null);
                alert.setContentText("Email inviata da: " + clientAddress + "\nA: " + originalSender + "\nOggetto: " + subject);
                alert.showAndWait();
                dialog.close();

            } catch (Exception ex) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Errore");
                errorAlert.setHeaderText("Errore di Salvataggio");
                errorAlert.setContentText("Non è stato possibile salvare la risposta. Errore: " + ex.getMessage());
                errorAlert.showAndWait();
            }
        });

        cancelButton.setOnAction(e -> dialog.close());

        vbox.getChildren().addAll(sendButton, cancelButton);
        Scene dialogScene = new Scene(vbox, 400, 300);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }
}
