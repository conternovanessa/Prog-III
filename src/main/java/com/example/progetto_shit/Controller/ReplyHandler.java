package com.example.progetto_shit.Controller;

import com.example.progetto_shit.Model.MessageStorage;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

public class ReplyHandler {

    private String senderAddress;
    private String clientAddress;
    private String originalSubject;

    public ReplyHandler(String senderAddress, String clientAddress, String originalSubject) {
        this.senderAddress = senderAddress;
        this.clientAddress = clientAddress;
        this.originalSubject = originalSubject;
    }

    public void replyToEmail() {
        // Mostra una finestra di dialogo per inserire il testo della risposta
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Rispondi Email");
        dialog.setHeaderText("Rispondi a: " + senderAddress);
        dialog.setContentText("Risposta:");

        // Ottiene il testo della risposta dall'utente
        String reply = dialog.showAndWait().orElse("");

        if (!reply.isEmpty()) {
            // Simulazione di invio della risposta
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Rispondi Email");
            alert.setHeaderText(null);
            alert.setContentText("Risposta inviata a: " + senderAddress + "\nContenuto: " + reply);
            alert.showAndWait();

            // Salva la risposta sotto il messaggio originale
            saveReply(senderAddress, clientAddress, originalSubject, reply);
        }
    }

    private void saveReply(String senderAddress, String recipientAddress, String originalSubject, String replyContent) {
        // Crea l'oggetto della risposta
        String subject = "Re: " + originalSubject;

        // Salva il messaggio di risposta nel file esistente
        // L'opzione `true` indica che si sta aggiungendo al file esistente
        MessageStorage.saveMessage(senderAddress, recipientAddress, subject, replyContent, true); // Risposta
        MessageStorage.saveMessage(recipientAddress, senderAddress, subject, replyContent, true); // Risposta

        // Log per debug
        System.out.println("Risposta inviata da " + senderAddress + " a " + recipientAddress);
        System.out.println("Oggetto: " + subject);
        System.out.println("Contenuto: " + replyContent);
    }
}
