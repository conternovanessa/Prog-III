package com.example.progetto_shit.Main;

import com.example.progetto_shit.Controller.ClientController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static javafx.application.Application.launch;

public class EmailApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setWidth(600);  // Larghezza della finestra
            primaryStage.setHeight(400); // Altezza della finestra

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/progetto_shit/View/client_view.fxml"));
            Parent root = loader.load();

            ClientController clientController = loader.getController();
            clientController.setPrimaryStage(primaryStage);

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Client Selection");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
