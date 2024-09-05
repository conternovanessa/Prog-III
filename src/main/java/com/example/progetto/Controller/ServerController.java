package com.example.progetto.Controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

public class ServerController {

    @FXML
    private Label statusLabel;
    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;

    private boolean serverRunning = false;
    private List<String> clientList = new ArrayList<>();
    private List<Stage> emailStages = new ArrayList<>();
    private Stage primaryStage;
    private CyclicBarrier barrier;

    private static final String CLIENT_FILE_PATH = "src/main/java/com/example/progetto/email.txt";

    @FXML
    public void initialize() {
        statusLabel.setText("Server Status: Stopped");
        loadClientsFromFile(CLIENT_FILE_PATH);
        barrier = new CyclicBarrier(clientList.size(), () -> System.out.println("Tutti i client sono pronti, ora possono connettersi!"));
    }

    @FXML
    private void handleStartServer() {
        startServer();
        Platform.runLater(this::openEmailControllers);
    }

    @FXML
    private void handleStopServer() {
        stopServer();
        Platform.runLater(this::closeAllEmailWindows);
    }

    private synchronized void startServer() {
        if (!serverRunning) {
            serverRunning = true;
            statusLabel.setText("Server Status: Running");
            // Qui puoi aggiungere la logica per avviare effettivamente il server
        }
    }

    private synchronized void stopServer() {
        if (serverRunning) {
            serverRunning = false;
            statusLabel.setText("Server Status: Stopped");
            // Qui puoi aggiungere la logica per fermare effettivamente il server
        }
    }

    private void openEmailControllers() {
        for (String client : clientList) {
            openEmailController(client);
        }
    }

    private void openEmailController(String client) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/progetto/View/email_view.fxml"));
            Parent root = loader.load();

            EmailController emailController = loader.getController();
            emailController.setClient(client);

            Stage emailStage = new Stage();
            emailController.setPrimaryStage(emailStage);
            emailStages.add(emailStage);

            Scene scene = new Scene(root);
            emailStage.setScene(scene);
            emailStage.setTitle("Email Viewer for " + client);
            emailStage.show();

            // Aggiungi un listener per gestire la chiusura della finestra
            emailStage.setOnCloseRequest(event -> {
                emailStages.remove(emailStage);
                if (emailStages.isEmpty()) {
                    primaryStage.show(); // Mostra la finestra del server quando tutte le finestre email sono chiuse
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeAllEmailWindows() {
        for (Stage stage : emailStages) {
            stage.close();
        }
        emailStages.clear();
        primaryStage.show(); // Mostra la finestra del server
    }

    private synchronized void loadClientsFromFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                clientList.add(line.trim());
            }
        } catch (IOException e) {
            System.err.println("Error reading the specified file: " + e.getMessage());
        }
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void initializeServer(List<String> clientList) {
        this.clientList = clientList;
        barrier = new CyclicBarrier(clientList.size(), () -> System.out.println("Tutti i client sono pronti, ora possono connettersi!"));

        // Se vuoi aprire automaticamente le finestre dei client all'avvio del server,
        // puoi decommentare la riga seguente:
        // Platform.runLater(this::openEmailControllers);
    }

}