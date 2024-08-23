package com.example.progetto_shit.Main;

import com.example.progetto_shit.Controller.ClientController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientApp extends Application {

    private static String clientAddress;

    public static void setClientAddress(String address) {
        clientAddress = address;
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Carica il file FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/progetto_shit/View/client_view.fxml"));
            Parent root = loader.load();

            // Ottieni il controller e passa l'indirizzo del client
            ClientController controller = loader.getController();
            controller.setServerAddress(clientAddress);

            primaryStage.setTitle("Client Mail Viewer - " + clientAddress);
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void launchClient(String address) {
        setClientAddress(address);
        launch(); // Avvia l'applicazione senza argomenti
    }

    public static void main(String[] args) {
        launch(); // Avvia l'applicazione senza argomenti
    }
}
