package com.example.progetto_shit.Controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerController {

    @FXML
    private Label statusLabel;

    @FXML
    private Button startButton;

    @FXML
    private Button stopButton;

    private boolean serverRunning = false;
    private ServerSocket serverSocket;
    private List<Socket> clientSockets; // Memorizza i socket dei client
    private Stage primaryStage;
    private List<String> clientList; // Lista dei client

    @FXML
    public void initialize() {
        statusLabel.setText("Server Status: Stopped");
        clientSockets = Collections.synchronizedList(new ArrayList<>());
    }

    @FXML
    private void handleStartServer() {
        startServer();
        Platform.runLater(() -> {
            // Avvia l'applicazione client direttamente
            openClientInterface("selezionare il client di dominio @progetto.com"); // Imposta un indirizzo client predefinito o modificabile
        });
    }

    @FXML
    private void handleStopServer() {
        stopServer();
        Platform.runLater(() -> {
            if (primaryStage != null) {
                primaryStage.close();
            }
        });
    }

    private synchronized void startServer() {
        if (!serverRunning) {
            serverRunning = true;
            statusLabel.setText("Server Status: Running");

            new Thread(() -> {
                try {
                    serverSocket = new ServerSocket(55555);
                    while (isServerRunning()) {
                        Socket clientSocket = serverSocket.accept();
                        synchronized (clientSockets) {
                            clientSockets.add(clientSocket);
                        }

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
            while (isServerRunning()) {
                // Leggi i dati dal client
                Object data = inputStream.readObject();
                // Gestisci i dati del client (es. aggiornare l'interfaccia del client)
                // Aggiungi la logica per l'elaborazione dei dati qui
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error handling client connection: " + e.getMessage());
        }
    }

    private synchronized void stopServer() {
        if (serverRunning) {
            serverRunning = false;
            statusLabel.setText("Server Status: Stopped");
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
                synchronized (clientSockets) {
                    for (Socket socket : clientSockets) {
                        if (!socket.isClosed()) {
                            socket.close();
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Error closing the server socket: " + e.getMessage());
            }
        }
    }

    private synchronized boolean isServerRunning() {
        return serverRunning;
    }

    private void openClientInterface(String clientAddress) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/progetto_shit/View/client_view.fxml"));
                Parent clientView = loader.load();

                ClientController clientController = loader.getController();
                clientController.setServerAddress(clientAddress);

                if (primaryStage != null) {
                    primaryStage.setScene(new Scene(clientView));
                    primaryStage.setTitle("Client Mail Viewer - " + clientAddress);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void setClientList(List<String> clients) {
        this.clientList = clients;
    }
}
