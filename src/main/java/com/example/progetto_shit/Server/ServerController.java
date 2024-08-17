package com.example.progetto_shit.Server;

import com.example.progetto_shit.Client.EmailClientManager;
import javafx.scene.control.Label;

import java.io.IOException;
import java.util.List;

public class ServerController {

    private static boolean serverRunning = false;

    public static void startServer(Label statusLabel, List<String> clientList) {
        if (!serverRunning) {
            serverRunning = true;
            statusLabel.setText("Server Status: Running");

            System.out.println("Server started.");
            System.out.println("Clients available: " + clientList);
        }
    }

    public static void stopServer(Label statusLabel) {
        if (serverRunning) {
            serverRunning = false;
            statusLabel.setText("Server Status: Stopped");

            System.out.println("Server stopped.");
        }
    }

    // Metodo che gestisce la selezione di un client
    public static void handleClientSelection(String clientName) {
        System.out.println("Handling selection for client: " + clientName);

        // Assumiamo che il server sia in esecuzione sulla stessa macchina e porta
        String serverAddress = "localhost";
        int serverPort = 12345; // Porta usata dal server

        // Creiamo un'istanza di EmailClientManager per questo client
        EmailClientManager clientManager = new EmailClientManager(serverAddress, serverPort);

        // Qui puoi inviare e ricevere messaggi usando clientManager
        try {
            // Simula l'invio di un messaggio dal client al server
            clientManager.sendMessageToServer("Hello from client: " + clientName);

            // Simula la ricezione di un messaggio dal server
            Object response = clientManager.receiveMessageFromServer();
            System.out.println("Received message from server: " + response);

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error during client-server communication: " + e.getMessage());
        }
    }
}
