package com.example.progetto_shit.Model;

import com.example.progetto_shit.Controller.ClientController;
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/progetto_shit/View/client_view.fxml"));
        Parent root = loader.load();

        // Passa l'indirizzo del server al controller del client
        ClientController controller = loader.getController();
        controller.initialize(serverAddress);

        primaryStage.setTitle("Email Client");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void launchClient(String serverAddress) throws Exception {
        // Passa l'indirizzo del server come argomento alla classe EmailClient
        EmailClient client = new EmailClient(serverAddress);
        client.start(new Stage()); // Questo metodo avvia l'applicazione, ma potrebbe non essere ideale per avviare più finestre
    }

    public static void main(String[] args) {
        launch(args);
    }
}
