package com.example.progetto.Controller;

import com.example.progetto.Model.MessageStorage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class NewMailHandler extends Application {

    private static final Logger logger = Logger.getLogger(NewMailHandler.class.getName());

    private static final List<String> VALID_RECIPIENTS = Arrays.asList(
            "fabiodelia@progetto.com",
            "filippoditto@progetto.com",
            "vanessaconterno@progetto.com"
    );

    private String currentSender;

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

    public String createNewMail() {
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

        Button sendButton = new Button("Invia");
        Button cancelButton = new Button("Annulla");

        sendButton.setOnAction(e -> {
            String recipientsText = recipientArea.getText();
            String subject = subjectField.getText();
            String body = bodyArea.getText();

            if (recipientsText.isEmpty() || subject.isEmpty() || body.isEmpty()) {
                logger.warning("Tentativo di invio email con campi mancanti");
                showErrorAlert("Dettagli mancanti", "Per favore, compila tutti i campi.");
                return;
            }

            List<String> recipientList = Arrays.stream(recipientsText.split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());

            for (String recipient : recipientList) {
                if (!VALID_RECIPIENTS.contains(recipient)) {
                    logger.warning("Tentativo di invio email a indirizzo non valido: " + recipient);
                    showErrorAlert("Indirizzo email non valido", "L'indirizzo email " + recipient + " non è valido.");
                    return;
                }
            }

            MessageStorage.saveMessage(currentSender, recipientList, subject, body);

            // Log the creation of new email for each receiver
            for (String receiver : recipientList) {
                logger.info("Nuova mail per receiver: " + receiver);
            }

            dialog.close();
        });

        cancelButton.setOnAction(e -> dialog.close());

        vbox.getChildren().addAll(sendButton, cancelButton);
        Scene dialogScene = new Scene(vbox, 400, 300);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
        return null;
    }

    private void showErrorAlert(String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
}