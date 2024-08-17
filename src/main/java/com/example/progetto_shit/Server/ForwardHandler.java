package com.example.progetto_shit.Server;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

public class ForwardHandler {

    private String clientAddress;

    public ForwardHandler(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public void forwardEmail() {
        // Mostra una finestra di dialogo per inserire l'indirizzo del destinatario
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Inoltra Email");
        dialog.setHeaderText("Inserisci l'indirizzo email del destinatario");
        dialog.setContentText("Destinatario:");

        String recipient = dialog.showAndWait().orElse("");

        if (!recipient.isEmpty()) {
            // Inoltra l'email al destinatario
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Inoltra Email");
            alert.setHeaderText(null);
            alert.setContentText("Email inoltrata a: " + recipient + "\nDa: " + clientAddress);
            alert.showAndWait();
        }
    }
}
