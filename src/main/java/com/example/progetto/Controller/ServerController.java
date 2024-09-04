package com.example.progetto.Controller;

import com.example.progetto.Controller.ClientController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

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
    private CyclicBarrier barrier;

    @FXML
    public void initialize() {
        statusLabel.setText("Server Status: Stopped");
        clientSockets = Collections.synchronizedList(new ArrayList<>());
        // Supponiamo di voler sincronizzare 3 client
        barrier = new CyclicBarrier(3, () -> System.out.println("Tutti i client sono pronti, ora possono connettersi!"));
    }

    @FXML
    private void handleStartServer() {
        startServer();
        Platform.runLater(() -> {
            // Avvia l'applicazione client direttamente per ogni client nella lista
            for (String clientAddress : clientList) {
                new Thread(() -> openClientInterface(clientAddress)).start();
            }
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
        try {
            barrier.await(); // Sincronizza l'apertura dei client
            Platform.runLater(() -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/progetto/View/client_view.fxml"));
                    Parent root = loader.load();

                    ClientController clientController = loader.getController();
                    clientController.setPrimaryStage(primaryStage); // Passa il riferimento del primaryStage

                    Scene scene = new Scene(root);
                    primaryStage.setScene(scene);
                    primaryStage.setTitle("Client View Ready");
                    primaryStage.show(); // Mostra la finestra del client
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void setClientList(List<String> clients) {
        this.clientList = clients;
    }
}
