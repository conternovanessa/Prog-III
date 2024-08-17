package com.example.progetto_shit.Client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class EmailClient extends Application {

    private String serverAddress;

    public EmailClient(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("client_view.fxml"));
        Parent root = loader.load();

        ClientController controller = loader.getController();
        controller.initialize(serverAddress); // Passa l'indirizzo del server al controller

        primaryStage.setTitle("Email Client");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
