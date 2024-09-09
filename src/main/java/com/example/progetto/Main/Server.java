package com.example.progetto.Main;

import com.example.progetto.Controller.ServerController;
import com.example.progetto.Model.EmailServer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Server extends Application {

    private List<String> clientList = new ArrayList<>();
    private static final String FILE_PATH = "src/main/java/com/example/progetto/email.txt";
    private static final int SERVER_PORT = 55555; // Definisci la porta del server qui

    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setTitle("Mail Server");
            primaryStage.setWidth(600);
            primaryStage.setHeight(400);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/progetto/View/server_view.fxml"));
            Parent root = loader.load();

            ServerController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);

            loadClientsFromFile(FILE_PATH);
            controller.initialize();

            // Avvia il server email in un thread separato
            startEmailServer();

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadClientsFromFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                clientList.add(line.trim());
            }
        } catch (IOException e) {
            System.err.println("Error reading the specified file: " + e.getMessage());
        }
    }

    private void startEmailServer() {
        EmailServer emailServer = new EmailServer(SERVER_PORT, clientList);
        Thread serverThread = new Thread(emailServer::start);
        serverThread.setDaemon(true);
        serverThread.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
