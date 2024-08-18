package com.example.progetto_shit.Server;

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

public class NewMailHandler extends Application {

    // Lista dei destinatari validi
    private static final List<String> VALID_RECIPIENTS = Arrays.asList(
            "fabiodelia@progetto.com",
            "filippoditto@progetto.com",
            "vanessaconterno@progetto.com"
    );

    public NewMailHandler(String selectedClient) {
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

        TextField recipientField = new TextField();
        recipientField.setPromptText("Inserisci l'indirizzo email del destinatario");

        TextField subjectField = new TextField();
        subjectField.setPromptText("Oggetto");

        TextArea bodyArea = new TextArea();
        bodyArea.setPromptText("Corpo");
        bodyArea.setPrefRowCount(5);

        VBox vbox = new VBox(10, new Label("Destinatario:"), recipientField,
                new Label("Oggetto:"), subjectField, new Label("Corpo:"), bodyArea);

        javafx.scene.control.Button sendButton = new javafx.scene.control.Button("Invia");
        javafx.scene.control.Button cancelButton = new javafx.scene.control.Button("Annulla");

        sendButton.setOnAction(e -> {
            String recipient = recipientField.getText();
            String subject = subjectField.getText();
            String body = bodyArea.getText();

            if (!VALID_RECIPIENTS.contains(recipient)) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Errore");
                errorAlert.setHeaderText("Indirizzo email non valido");
                errorAlert.setContentText("L'indirizzo email inserito non è valido.\nI destinatari validi sono:\n" +
                        "• fabiodelia@progetto.com\n" +
                        "• filippoditto@progetto.com\n" +
                        "• vanessaconterno@progetto.com");
                errorAlert.showAndWait();
                return;
            }

            if (!subject.isEmpty() && !body.isEmpty()) {
                // Invio l'email a MessageStorage
                MessageStorage.saveMessage(recipient, subject, body);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Nuova Mail");
                alert.setHeaderText(null);
                alert.setContentText("Mail inviata a: " + recipient + "\nOggetto: " + subject + "\nCorpo: " + body);
                alert.showAndWait();
                dialog.close();
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Errore");
                errorAlert.setHeaderText("Dettagli mancanti");
                errorAlert.setContentText("Per favore, compila tutti i campi.");
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
