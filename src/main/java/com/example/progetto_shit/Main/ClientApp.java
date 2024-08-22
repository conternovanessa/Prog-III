package com.example.progetto_shit.Main;

import com.example.progetto_shit.Controller.ClientController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientApp extends Application {

    private String clientAddress;

    public ClientApp() {
        // Necessario per Application.launch()
    }

    public ClientApp(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Carica il file FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/progetto_shit/View/client_view.fxml"));
            Parent root = loader.load();

            // Ottieni il controller e passa l'indirizzo del client
            ClientController controller = loader.getController();
            controller.initialize(clientAddress);

            primaryStage.setTitle("Client Mail Viewer - " + clientAddress);
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void launchClient(String clientAddress) {
        // Lancia l'app JavaFX e passa l'indirizzo del client come parametro
        Application.launch(ClientApp.class, clientAddress);
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            launchClient(args[0]);
        } else {
            System.out.println("Client address not provided.");
        }
    }
}
