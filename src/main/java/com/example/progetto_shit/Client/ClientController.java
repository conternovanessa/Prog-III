package com.example.progetto_shit.Client;

import javafx.fxml.FXML;

public class ClientController {

    private String serverAddress;

    @FXML
    public void initialize(String serverAddress) {
        this.serverAddress = serverAddress;
        // Usa serverAddress per inizializzare la connessione o altre logiche
        System.out.println("Connected to server: " + serverAddress);
    }
}
