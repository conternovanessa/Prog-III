package com.example.progetto_shit.Main;

import com.example.progetto_shit.Controller.ClientController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientApp extends Application {

    public static String clientAddress;

    public static void setClientAddress(String address) {
        clientAddress = address;
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/progetto_shit/View/client_view.fxml"));
            Parent root = loader.load();

            // Passa l'indirizzo del client al controller
            ClientController controller = loader.getController();
            controller.setServerAddress(clientAddress);

            primaryStage.setTitle("Client Mail Viewer - " + clientAddress);
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void launchClient(Stage primaryStage, String address) {
        setClientAddress(address);

        try {
            primaryStage.setWidth(600);  // Larghezza della finestra
            primaryStage.setHeight(400); // Altezza della finestra


            FXMLLoader loader = new FXMLLoader(ClientApp.class.getResource("/com/example/progetto_shit/View/client_view.fxml"));
            Parent root = loader.load();

            // Passa l'indirizzo del client al controller
            ClientController controller = loader.getController();
            controller.setServerAddress(address);

            primaryStage.setTitle("Client Mail Viewer - " + address);
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
