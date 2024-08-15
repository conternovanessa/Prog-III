
package com.example.progetto_shit.Server;
import com.example.progetto_shit.Client.*;

import javafx.application.Application;
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

    public static void main(String[] args) {
        // Avvia il server (qui non è incluso, ma è lo stesso codice di prima)
        new Thread(Server::startServer).start();

        // Avvia JavaFX
        launch(args);
    }

    private static void startServer() {
        // Codice per avviare il server socket e gestire i client
        // (Lo stesso codice usato in precedenza per gestire i ClientHandler)
    }

    @Override
    public void start(Stage primaryStage) {
        // Leggi le email dal file
        List<String> emails = readEmailsFromFile();

        // Crea l'interfaccia JavaFX
        VBox root = new VBox();
        root.setSpacing(10);

        Label label = new Label("Scegli una delle seguenti email:");

        // Crea i pulsanti per ogni email
        for (String email : emails) {
            Button emailButton = new Button(email);
            emailButton.setOnAction(e -> handleEmailSelection(email));
            root.getChildren().add(emailButton);
        }

        Scene scene = new Scene(root, 300, 200);
        primaryStage.setTitle("Scegli una Email");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private List<String> readEmailsFromFile() {
        try {
            // Specifica il percorso assoluto del file email.txt
            String absolutePath = Paths.get("src/main/java/com/example/progetto_shit/email.txt").toAbsolutePath().toString();
            Path path = Paths.get(absolutePath);
            System.out.println("Tentativo di lettura del file da: " + path.toString());
            return Files.readAllLines(path);
        } catch (IOException e) {
            System.err.println("Errore nella lettura del file: " + e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    private void handleEmailSelection(String email) {
        System.out.println("Email selezionata: " + email);

        // Avvia il client corrispondente in un nuovo thread
        Thread clientThread = new Thread(() -> {
            EmailClient client = new EmailClient("localhost", PORT, email);
            client.run();  // Lancia l'interfaccia grafica del client
        });
        clientThread.start();
    }
}
