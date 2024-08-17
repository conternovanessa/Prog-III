package com.example.progetto_shit.Server;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

public class NewMailHandler {

    private String clientAddress;

    public NewMailHandler(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public void createNewMail() {
        // Mostra una finestra di dialogo per inserire l'oggetto e il corpo dell'email
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nuova Mail");
        dialog.setHeaderText("Inserisci i dettagli della nuova email");
        dialog.setContentText("Oggetto:");

        String subject = dialog.showAndWait().orElse("");

        if (!subject.isEmpty()) {
            dialog.setContentText("Corpo:");
            String body = dialog.showAndWait().orElse("");

            if (!body.isEmpty()) {
                // Invia l'email al server o esegui altre azioni necessarie
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Nuova Mail");
                alert.setHeaderText(null);
                alert.setContentText("Mail inviata a: " + clientAddress + "\nOggetto: " + subject + "\nCorpo: " + body);
                alert.showAndWait();
            }
        }
    }
}
