package com.example.progetto_shit.Controller;

import com.example.progetto_shit.Model.MessageStorage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

import static com.example.progetto_shit.Main.ClientApp.clientAddress;

public class NewMailHandler extends Application {

    // Lista dei destinatari validi
    private static final List<String> VALID_RECIPIENTS = Arrays.asList(
            "fabiodelia@progetto.com",
            "filippoditto@progetto.com",
            "vanessaconterno@progetto.com"
    );

    private String currentSender; // Email del mittente

    public NewMailHandler(String currentSender) {
        this.currentSender = currentSender;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Applicazione di Email");
        VBox root = new VBox();
        Scene scene = new Scene(root, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();

        createNewMail();
    }

    public void createNewMail() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Nuova Email");

        TextArea recipientArea = new TextArea();
        recipientArea.setPromptText("Inserisci gli indirizzi email dei destinatari separati da virgola");

        TextField subjectField = new TextField();
        subjectField.setPromptText("Oggetto");

        TextArea bodyArea = new TextArea();
        bodyArea.setPromptText("Corpo");
        bodyArea.setPrefRowCount(5);

        VBox vbox = new VBox(10,
                new Label("Destinatari:"), recipientArea,
                new Label("Oggetto:"), subjectField,
                new Label("Corpo:"), bodyArea);

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

                // Invio l'email a MessageStorage includendo il mittente (dinamico)
                MessageStorage.saveMessage(currentSender, recipient, subject, body, false);
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Nuova Mail");
            alert.setHeaderText(null);
            alert.setContentText("Mail inviata da: " + currentSender + "\nA: " + recipients + "\nOggetto: " + subject + "\nCorpo: " + body);
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
