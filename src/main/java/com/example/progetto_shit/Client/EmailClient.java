package com.example.progetto_shit.Client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class EmailClient extends Application {
    private static String userEmail;

    public static void launchClient(String userEmail) {
        EmailClient.userEmail = userEmail;
        Platform.runLater(() -> {
            // Lancia l'applicazione JavaFX
            launch(EmailClient.class);
        });
    }

    @Override
    public void start(Stage primaryStage) {
        // Il metodo start deve essere implementato, ma non utilizzato direttamente.
        // Il client viene gestito tramite EmailClientManager.
    }
}