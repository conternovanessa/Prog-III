package com.example.progetto_shit.Server;

public class ReceivedMailsHandler {

    private String clientAddress;

    public ReceivedMailsHandler(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public String getReceivedMails() {
        // Implementa la logica per ottenere i messaggi ricevuti
        // Per esempio, leggi da un file o da una sorgente dati
        // Esempio di messaggi ricevuti (da sostituire con la logica reale)
        return "1. Email da client1@example.com\n2. Email da client2@example.com\n3. Email da client3@example.com";
    }
}
