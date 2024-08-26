package com.example.progetto_shit.Controller;

import com.example.progetto_shit.Model.MessageStorage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

public class ReplyHandler {

    // Lista dei destinatari validi
    private static final List<String> VALID_RECIPIENTS = Arrays.asList(
            "fabiodelia@progetto.com",
            "filippoditto@progetto.com",
            "vanessaconterno@progetto.com"
    );

    private String clientAddress; // Email del mittente
    private String originalSender; // Email del mittente dell'email a cui si risponde
    private String originalSubject; // Oggetto dell'email a cui si risponde

    public ReplyHandler(String clientAddress, String originalSender, String originalSubject) {
        this.clientAddress = clientAddress;
        this.originalSender = originalSender;
        this.originalSubject = originalSubject;
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

        VBox vbox = new VBox(10,
                new javafx.scene.control.Label("Oggetto:"), subjectField,
                new javafx.scene.control.Label("Corpo:"), bodyArea);

        javafx.scene.control.Button sendButton = new javafx.scene.control.Button("Invia");
        javafx.scene.control.Button cancelButton = new javafx.scene.control.Button("Annulla");

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

            // Controlla se il mittente della risposta è un destinatario valido
            if (!VALID_RECIPIENTS.contains(originalSender)) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Errore");
                errorAlert.setHeaderText("Indirizzo email non valido");
                errorAlert.setContentText("Il mittente dell'email a cui si sta rispondendo non è valido.");
                errorAlert.showAndWait();
                return;
            }

            // Rispondi all'email salvandola tramite MessageStorage
            MessageStorage.saveMessage(
                    clientAddress,     // Mittente dell'email
                    Arrays.asList(originalSender), // Lista dei destinatari (solo il mittente originale)
                    subject,           // Oggetto dell'email
                    body,              // Corpo dell'email
                    true               // È una risposta
            );

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Risposta Email");
            alert.setHeaderText(null);
            alert.setContentText("Email inviata da: " + clientAddress + "\nA: " + originalSender + "\nOggetto: " + subject);
            alert.showAndWait();
            dialog.close();
        });

        cancelButton.setOnAction(e -> dialog.close());

        vbox.getChildren().addAll(sendButton, cancelButton);
        Scene dialogScene = new Scene(vbox, 400, 300);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }
}