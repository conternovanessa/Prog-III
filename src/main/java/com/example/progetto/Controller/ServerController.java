package com.example.progetto.Controller;

import com.example.progetto.Model.EmailObservable;
import com.example.progetto.Model.EmailObserver;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ServerController implements EmailObserver {

    @FXML
    private Label statusLabel;

    @FXML
    private TextArea connectedClientsTextArea;

    @FXML
    private VBox clientButtonsContainer;

    private boolean serverRunning = false;
    private Set<String> clientSet = new HashSet<>();
    private List<Stage> emailStages = new ArrayList<>();
    private Stage primaryStage;
    private EmailObservable emailObservable;

    private static final String CLIENT_FILE_PATH = "src/main/java/com/example/progetto/email.txt";
    private static Logger logger = Logger.getLogger(ServerController.class.getName());

    @FXML
    public void initialize() {
        initializeLogger();
        statusLabel.setText("Server Status: Stopped");
        loadClientsFromFile(CLIENT_FILE_PATH);
        updateConnectedClientsDisplay();
        emailObservable = new EmailObservable();
        emailObservable.addObserver(this);

        logger.info("Server controller initialized");
    }

    private void initializeLogger() {
        try {
            LogManager.getLogManager().readConfiguration(
                    ServerController.class.getResourceAsStream("/logging.properties"));

            // Crea la cartella "logs" se non esiste già
            Path logsDir = Paths.get(System.getProperty("user.dir"), "logs");
            if (!Files.exists(logsDir)) {
                Files.createDirectory(logsDir);
            }
        } catch (IOException e) {
            System.err.println("Error loading logging configuration: " + e.getMessage());
        }

        // Imposta il logger per l'intero sistema
        logger = Logger.getLogger("");
    }
    @FXML
    private void handleStartServer() {
        if (!serverRunning) {
            startServer();
            Platform.runLater(this::createClientButtons);
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

        logger.info("Server started");
    }

    private synchronized void stopServer() {
        serverRunning = false;
        statusLabel.setText("Server Status: Stopped");
        updateConnectedClientsDisplay();
        clientButtonsContainer.getChildren().clear();

        logger.info("Server stopped");
    }

    private void createClientButtons() {
        clientButtonsContainer.getChildren().clear();
        for (String client : clientSet) {
            Button clientButton = new Button(client);
            clientButton.setOnAction(event -> openEmailController(client));
            clientButtonsContainer.getChildren().add(clientButton);
        }
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