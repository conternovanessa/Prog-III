package com.example.progetto_shit.Controller;

import com.example.progetto_shit.Main.ClientApp;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerController {

    @FXML
    private Label statusLabel;

    @FXML
    private VBox clientListBox;

    private boolean serverRunning = false;

    public void initializeServer() {
        List<String> clientList = getClientListFromFile("client_list.txt");
        displayClients(clientList);
        startServer(clientList);
    }

    public void startServer(List<String> clientList) {
        if (!serverRunning) {
            serverRunning = true;
            statusLabel.setText("Server Status: Running");

            System.out.println("Server started.");
            System.out.println("Clients available: " + clientList);

            new Thread(() -> {
                try (ServerSocket serverSocket = new ServerSocket(55555)) {
                    while (serverRunning) {
                        Socket clientSocket = serverSocket.accept();
                        new ClientHandler(clientSocket, clientList).start();
                    }
                } catch (IOException e) {
                    System.err.println("Error in server socket: " + e.getMessage());
                }
            }).start();
        }
    }

    public void stopServer() {
        if (serverRunning) {
            serverRunning = false;
            statusLabel.setText("Server Status: Stopped");
            System.out.println("Server stopped.");
        }
    }

    @FXML
    private void handleStartServer() {
        initializeServer();
    }

    @FXML
    private void handleStopServer() {
        stopServer();
    }

    private void displayClients(List<String> clientList) {
        clientListBox.getChildren().clear();
        for (String client : clientList) {
            Button clientButton = new Button("Connect to " + client);
            clientButton.setOnAction(event -> openClientInterface(client));
            clientListBox.getChildren().add(clientButton);
        }
    }

    private List<String> getClientListFromFile(String filePath) {
        List<String> clientList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                clientList.add(line.trim());
            }
        } catch (IOException e) {
            System.err.println("Error reading client list file: " + e.getMessage());
        }
        return clientList;
    }

    private void openClientInterface(String clientAddress) {
        Platform.runLater(() -> {
            try {
                // Avvia l'applicazione client con l'indirizzo selezionato
                ClientApp.launchClient(clientAddress);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
