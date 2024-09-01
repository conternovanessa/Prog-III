package com.example.progetto.Main;

import com.example.progetto.Controller.ServerController;
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

    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setTitle("Mail Server");

            primaryStage.setWidth(600);  // Larghezza della finestra
            primaryStage.setHeight(400); // Altezza della finestra

            // Carica il file FXML e ottieni la root della scena
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/progetto/View/server_view.fxml"));
            Parent root = loader.load();

            // Ottieni l'istanza del ServerController associato al file FXML
            ServerController controller = loader.getController();

            // Passa il riferimento al primaryStage al controller
            controller.setPrimaryStage(primaryStage);

            // Carica i client dal file e passali al controller
            loadClientsFromFile(FILE_PATH);
            controller.setClientList(clientList);

            // Imposta la scena e mostra lo stage
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Metodo per caricare i client da file
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

    public static void main(String[] args) {
        launch(args);
    }
}
