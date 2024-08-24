package com.example.progetto_shit.Controller;

import com.example.progetto_shit.Main.ClientApp;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

public class ServerController {

    @FXML
    private Label statusLabel;

    @FXML
    private VBox clientListBox;

    private boolean serverRunning = false;
    private ServerSocket serverSocket;
    private List<Socket> clientSockets; // Memorizza i socket dei client

    @FXML
    public void initialize() {
        statusLabel.setText("Server Status: Stopped");
        clientSockets = new ArrayList<>();
    }

    public void initializeServer(List<String> clientList) {
        displayClients(clientList);
        startServer();
    }

    @FXML
    private void handleStartServer() {
        startServer();
    }

    @FXML
    private void handleStopServer() {
        stopServer();
    }

    public void startServer() {
        if (!serverRunning) {
            serverRunning = true;
            statusLabel.setText("Server Status: Running");

            new Thread(() -> {
                try {
                    serverSocket = new ServerSocket(55555);
                    while (serverRunning) {
                        Socket clientSocket = serverSocket.accept();
                        clientSockets.add(clientSocket);

                        // Gestisci la connessione con il client in un nuovo thread
                        new Thread(() -> handleClientConnection(clientSocket)).start();
                    }
                } catch (IOException e) {
                    System.err.println("Error in server socket: " + e.getMessage());
                }
            }).start();
        }
    }

    private void handleClientConnection(Socket clientSocket) {
        try (ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream())) {
            while (serverRunning) {
                // Leggi i dati dal client
                Object data = inputStream.readObject();
                // Gestisci i dati del client (es. aggiornare l'interfaccia del client)
                // Aggiungi la logica per l'elaborazione dei dati qui
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error handling client connection: " + e.getMessage());
        }
    }

    public void stopServer() {
        if (serverRunning) {
            serverRunning = false;
            statusLabel.setText("Server Status: Stopped");
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
                for (Socket socket : clientSockets) {
                    if (!socket.isClosed()) {
                        socket.close();
                    }
                }
            } catch (IOException e) {
                System.err.println("Error closing the server socket: " + e.getMessage());
            }
        }
    }

    private void displayClients(List<String> clientList) {
        clientListBox.getChildren().clear();

        for (String client : clientList) {
            Button clientButton = new Button(client);
            clientButton.setOnAction(event -> handleClientSelection(client));
            clientListBox.getChildren().add(clientButton);
        }
    }

    private void handleClientSelection(String client) {
        Platform.runLater(() -> {
            // Avvia il client in una nuova finestra JavaFX
            Stage primaryStage = new Stage();
            ClientApp.launchClient(primaryStage, client);
        });
    }
}
