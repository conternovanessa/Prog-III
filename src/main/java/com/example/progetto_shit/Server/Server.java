package com.example.progetto_shit.Server;

import com.example.progetto_shit.Client.EmailClientManager;
import javafx.application.Application;
import javafx.application.Platform; // Aggiungi questa importazione
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class Server extends Application {
    private static final int PORT = 55555;
    private static Stage primaryStage;

    public static void main(String[] args) {
        // Avvia il server
        new Thread(Server::startServer).start();

        // Avvia JavaFX
        launch(args);
    }

    private static void startServer() {
        // Codice per avviare il server socket e gestire i client
        // (Lo stesso codice usato in precedenza per gestire i ClientHandler)
    }

    @Override
    public void start(Stage stage) {
        Server.primaryStage = stage;
        showEmailSelection();
    }

    public static void showEmailSelection() {
        if (primaryStage != null) {
            primaryStage.close(); // Chiudi la finestra corrente se esiste
        }

        Platform.runLater(() -> {
            Stage stage = new Stage();
            VBox root = new VBox();
            root.setSpacing(10);

            Label label = new Label("Scegli una delle seguenti email:");
            List<String> emails = readEmailsFromFile();

            for (String email : emails) {
                Button emailButton = new Button(email);
                emailButton.setOnAction(e -> handleEmailSelection(email));
                root.getChildren().add(emailButton);
            }

            Scene scene = new Scene(root, 300, 200);
            stage.setTitle("Scegli una Email");
            stage.setScene(scene);
            stage.show();
            Server.primaryStage = stage; // Aggiorna il riferimento al primaryStage
        });
    }

    private static List<String> readEmailsFromFile() {
        try {
            String absolutePath = Paths.get("src/main/java/com/example/progetto_shit/email.txt").toAbsolutePath().toString();
            Path path = Paths.get(absolutePath);
            System.out.println("Tentativo di lettura del file da: " + path.toString());
            return Files.readAllLines(path);
        } catch (IOException e) {
            System.err.println("Errore nella lettura del file: " + e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    private static void handleEmailSelection(String email) {
        System.out.println("Email selezionata: " + email);

        // Usa EmailClientManager per avviare il client
        EmailClientManager.getInstance().startApplication(email);
    }
}