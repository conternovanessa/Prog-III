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

public class ForwardHandler {

    // Lista dei destinatari validi
    private static final List<String> VALID_RECIPIENTS = Arrays.asList(
            "fabiodelia@progetto.com",
            "filippoditto@progetto.com",
            "vanessaconterno@progetto.com"
    );

    private String clientAddress;  // Email del mittente

    public ForwardHandler(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public void forwardEmail(String selectedEmail) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Inoltra Email");

        // Usa un TextArea per inserire più destinatari separati da virgole
        TextArea recipientArea = new TextArea();
        recipientArea.setPromptText("Inserisci gli indirizzi email dei destinatari separati da virgola");

        TextField subjectField = new TextField();
        subjectField.setPromptText("Oggetto");
        // Setta il soggetto al valore del soggetto dell'email originale
        String[] emailLines = selectedEmail.split("\n", 3);
        String originalSubject = emailLines.length > 1 ? emailLines[1].replace("Subject: ", "") : "No Subject";
        subjectField.setText("Fwd: " + originalSubject);

        TextArea bodyArea = new TextArea();
        bodyArea.setPromptText("Corpo");
        bodyArea.setPrefRowCount(5);
        // Prepopola il corpo con l'email originale
        String originalBody = emailLines.length > 2 ? emailLines[2] : "No Content";
        bodyArea.setText("\n\nInoltro dell'email originale:\n" + originalBody);

        VBox vbox = new VBox(10,
                new javafx.scene.control.Label("Destinatari:"), recipientArea,
                new javafx.scene.control.Label("Oggetto:"), subjectField,
                new javafx.scene.control.Label("Corpo:"), bodyArea);

        javafx.scene.control.Button sendButton = new javafx.scene.control.Button("Invia");
        javafx.scene.control.Button cancelButton = new javafx.scene.control.Button("Annulla");

        sendButton.setOnAction(e -> {
            String recipients = recipientArea.getText();
            String subject = subjectField.getText();
            String body = bodyArea.getText();

            if (recipients.isEmpty() || subject.isEmpty() || body.isEmpty()) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Errore");
                errorAlert.setHeaderText("Dettagli mancanti");
                errorAlert.setContentText("Per favore, compila tutti i campi.");
                errorAlert.showAndWait();
                return;
            }

            // Dividi i destinatari usando la virgola come separatore e rimuovi eventuali spazi
            String[] recipientArray = recipients.split(",");
            for (String recipient : recipientArray) {
                recipient = recipient.trim(); // Rimuove gli spazi in eccesso
                if (!VALID_RECIPIENTS.contains(recipient)) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Errore");
                    errorAlert.setHeaderText("Indirizzo email non valido");
                    errorAlert.setContentText("L'indirizzo email " + recipient + " non è valido.");
                    errorAlert.showAndWait();
                    return;
                }

                // Genera il nome del file basato sull'email del destinatario e l'oggetto
                String fileName = recipient + "." + subject.replaceAll("\\s+", "_") + ".mail";

                // Inoltra l'email salvandola tramite MessageStorage
                MessageStorage.saveMessage(clientAddress, recipient, subject, body, false);
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Inoltra Email");
            alert.setHeaderText(null);
            alert.setContentText("Email inoltrata da: " + clientAddress + "\nA: " + recipients + "\nOggetto: " + subject);
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
