package com.example.progetto_shit.Controller;

import com.example.progetto_shit.Main.ClientApp;
import javafx.application.Platform;
import javafx.fxml.FXML;
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

    @FXML
    private VBox clientListBox;  // Contenitore per i pulsanti dei client

    private boolean serverRunning = false;
    private ServerSocket serverSocket;
    private List<Socket> clientSockets; // Memorizza i socket dei client
    private Stage primaryStage;

    @FXML
    public void initialize() {
        statusLabel.setText("Server Status: Stopped");
        clientSockets = Collections.synchronizedList(new ArrayList<>());
    }

    @FXML
    private void handleStartServer() {
        startServer();
        // Mostra i client disponibili sulla stessa finestra
        displayClientList();
    }

    @FXML
    private void handleStopServer() {
        stopServer();
        // Chiudi l'interfaccia
        Platform.runLater(() -> {
            Stage stage = (Stage) stopButton.getScene().getWindow();
            stage.close();
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

    private void displayClientList() {
        // Simuliamo una lista di client per la dimostrazione
        List<String> clients = getClientListFromFile("email.txt");

        clientListBox.getChildren().clear(); // Pulisci eventuali elementi esistenti
        for (String client : clients) {
            Button clientButton = new Button("Connect to " + client);
            clientButton.setOnAction(event -> openClientInterface(client));
            clientListBox.getChildren().add(clientButton);
        }
    }

    private void openClientInterface(String clientAddress) {
        Platform.runLater(() -> {
            try {
                // Avvia l'interfaccia client nella stessa finestra
                ClientApp.launchClient(primaryStage, clientAddress);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public List<String> getClientListFromFile(String file) {
        List<String> clients = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("Email trovata: " + line);
                clients.add(line); // Aggiungi l'email alla lista
            }
        } catch (FileNotFoundException e) {
            System.out.println("File " + file + " non trovato: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Errore durante la lettura del file: " + e.getMessage());
        }
        return clients;
    }
}
