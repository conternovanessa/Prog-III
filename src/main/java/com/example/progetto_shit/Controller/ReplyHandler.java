package com.example.progetto_shit.Controller;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

public class ReplyHandler {

    private String senderAddress;
    private String clientAddress;

    public ReplyHandler(String senderAddress, String clientAddress) {
        this.senderAddress = senderAddress;
        this.clientAddress = clientAddress;
    }

    public void replyToEmail() {
        // Mostra una finestra di dialogo per inserire il testo della risposta
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Rispondi Email");
        dialog.setHeaderText("Rispondi a: " + senderAddress);
        dialog.setContentText("Risposta:");

        String reply = dialog.showAndWait().orElse("");

        if (!reply.isEmpty()) {
            // Simulazione di invio della risposta
            // Qui si potrebbe aggiungere il codice per inviare l'email tramite un sistema di gestione email
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Rispondi Email");
            alert.setHeaderText(null);
            alert.setContentText("Risposta inviata a: " + senderAddress + "\nContenuto: " + reply);
            alert.showAndWait();

            // Qui si potrebbe chiamare un metodo che effettivamente invia l'email
            sendReply(senderAddress, clientAddress, reply);
        }
    }

    private void sendReply(String senderAddress, String clientAddress, String replyContent) {
        // Implementa la logica di invio della mail qui
        // Ad esempio, potresti salvare la risposta in un database o inviarla tramite un server di posta
        System.out.println("Inviando la risposta a " + senderAddress + " da " + clientAddress);
        System.out.println("Contenuto: " + replyContent);
    }
}
