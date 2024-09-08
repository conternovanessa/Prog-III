package com.example.progetto.Controller;

import com.example.progetto.Model.EmailObservable;
import com.example.progetto.Model.EmailObserver;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ServerController implements EmailObserver {

    @FXML
    private Label statusLabel;

    @FXML
    private TextArea connectedClientsTextArea;

    private boolean serverRunning = false;
    private Set<String> clientSet = new HashSet<>();
    private List<Stage> emailStages = new ArrayList<>();
    private Stage primaryStage;
    private EmailObservable emailObservable;

    private static final String CLIENT_FILE_PATH = "src/main/java/com/example/progetto/email.txt";

    @FXML
    public void initialize() {
        statusLabel.setText("Server Status: Stopped");
        loadClientsFromFile(CLIENT_FILE_PATH);
        updateConnectedClientsDisplay();
        emailObservable = new EmailObservable();
        emailObservable.addObserver(this);
    }

    @FXML
    private void handleStartServer() {
        if (!serverRunning) {
            startServer();
            Platform.runLater(this::openEmailControllers);
        }
    }

    @FXML
    private void handleStopServer() {
        stopServer();
        Platform.runLater(this::closeAllEmailWindows);
    }

    private synchronized void startServer() {
        serverRunning = true;
        statusLabel.setText("Server Status: Running");
        updateConnectedClientsDisplay();
    }

    private synchronized void stopServer() {
        serverRunning = false;
        statusLabel.setText("Server Status: Stopped");
        updateConnectedClientsDisplay();
    }

    @Override
    public void update(List<String> emails) {
        if (!emails.isEmpty()) {
            String lastEmail = emails.get(emails.size() - 1);
            String[] emailParts = lastEmail.split("\n");
            if (emailParts.length > 2) {
                String recipient = emailParts[2].replace("To: ", "");
                Platform.runLater(() -> {
                    updateConnectedClientsDisplay("New email received for " + recipient);
                    System.out.println("New email notification: " + recipient);
                });
            }
        }
    }

    private void updateConnectedClientsDisplay() {
        updateConnectedClientsDisplay("");
    }

    private void updateConnectedClientsDisplay(String additionalInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("Connected Clients:\n");
        if (serverRunning) {
            for (String client : clientSet) {
                sb.append(client).append("\n");
            }
        } else {
            sb.append("No clients connected (Server is stopped)");
        }
        if (!additionalInfo.isEmpty()) {
            sb.append("\n").append(additionalInfo);
        }
        connectedClientsTextArea.setText(sb.toString());
        System.out.println("Updated display: " + sb.toString());
    }

    private void openEmailControllers() {
        for (String client : clientSet) {
            if (emailStages.stream().noneMatch(stage -> stage.getTitle().contains(client))) {
                openEmailController(client);
            }
        }
    }

    private void openEmailController(String client) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/progetto/View/email_view.fxml"));
            Parent root = loader.load();

            EmailController emailController = loader.getController();
            emailController.setClient(client);
            emailController.setServerController(this);

            Stage emailStage = new Stage();
            emailController.setPrimaryStage(emailStage);
            emailStages.add(emailStage);

            Scene scene = new Scene(root);
            emailStage.setScene(scene);
            emailStage.setTitle("Email Viewer for " + client);
            emailStage.show();

            emailStage.setOnCloseRequest(event -> {
                emailStages.remove(emailStage);
                if (emailStages.isEmpty() && primaryStage != null) {
                    primaryStage.show();
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
        if (primaryStage != null) {
            primaryStage.show();
        }
    }

    private synchronized void loadClientsFromFile(String filePath) {
        clientSet.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                clientSet.add(line.trim());
            }
        } catch (IOException e) {
            System.err.println("Error reading the specified file: " + e.getMessage());
        }
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void addNewEmail(String email) {
        emailObservable.addEmail(email);
        System.out.println("New email added: " + email);
    }
}