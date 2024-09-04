package com.example.progetto.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientController {
    private Stage primaryStage;

    @FXML
    private Label serverAddressLabel;

    private String selectedClient;

    @FXML
    private VBox emailBox;

    private List<String> clientList = new ArrayList<>();
    private String serverAddress;

    private static final String FILE_PATH = "src/main/java/com/example/progetto/email.txt";

    @FXML
    public void initialize() {
        if (serverAddress != null) {
            serverAddressLabel.setText("Server Address: " + serverAddress);
        }

        loadClientsFromFile(FILE_PATH);
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
        if (serverAddressLabel != null) {
            serverAddressLabel.setText("Server Address: " + serverAddress);
        }
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    private void loadClientsFromFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String client = line.trim();
                clientList.add(client);

                Button clientButton = new Button("Connect to " + client);
                clientButton.setOnAction(event -> {
                    selectedClient = client;  // Imposta il client selezionato
                    openEmailController();    // Apre il controller email
                });

                emailBox.getChildren().add(clientButton);
            }
        } catch (IOException e) {
            System.err.println("Error reading the specified file: " + e.getMessage());
        }
    }

    @FXML
    private void openEmailController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/progetto/View/email_view.fxml"));
            Parent root = loader.load();

            // Ottieni il controller dell'EmailController
            EmailController emailController = loader.getController();

            // Passa il client selezionato al nuovo controller
            emailController.setClient(selectedClient);

            // Crea una nuova finestra (Stage) per il client
            Stage clientStage = new Stage();
            emailController.setPrimaryStage(clientStage);

            Scene scene = new Scene(root);
            clientStage.setScene(scene);
            clientStage.setTitle("Email Viewer for " + selectedClient);
            clientStage.show(); // Mostra la nuova finestra

            // La finestra principale di ClientController rimane aperta
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleBack() {
        if (primaryStage == null) {
            System.err.println("primaryStage is null. Please set it before opening the previous view.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/progetto/View/server_view.fxml"));
            Parent root = loader.load();

            // Ottieni il controller della vista del server (se necessario)
            ServerController serverController = loader.getController();
            if (serverController != null) {
                serverController.setPrimaryStage(primaryStage);  // Passa il primaryStage, se necessario
            }

            // Imposta la nuova scena
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Server Selection");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading the server view: " + e.getMessage());
        }
    }
}
