package com.example.progetto_shit.Server;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

public class ReplyHandler {

    private String clientAddress;

    public ReplyHandler(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public void replyToEmail() {
        // Mostra una finestra di dialogo per inserire il testo della risposta
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Rispondi Email");
        dialog.setHeaderText("Inserisci il testo della risposta");
        dialog.setContentText("Risposta:");

        String reply = dialog.showAndWait().orElse("");

        if (!reply.isEmpty()) {
            // Invia la risposta
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Rispondi Email");
            alert.setHeaderText(null);
            alert.setContentText("Risposta inviata a: " + clientAddress + "\nContenuto: " + reply);
            alert.showAndWait();
        }
    }
}
