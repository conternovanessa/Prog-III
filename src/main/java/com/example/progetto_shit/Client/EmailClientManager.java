package com.example.progetto_shit.Client;

import com.example.progetto_shit.Server.Server;
import javafx.application.Platform; // Aggiungi questa importazione
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EmailClientManager {
    private static EmailClientManager instance;
    private Stage primaryStage;

    private EmailClientManager() {
    }

    public static synchronized EmailClientManager getInstance() {
        if (instance == null) {
            instance = new EmailClientManager();
        }
        return instance;
    }

    public void startApplication(String userEmail) {
        Platform.runLater(() -> {
            if (primaryStage != null && primaryStage.isShowing()) {
                primaryStage.close(); // Chiude la finestra esistente
            }
            primaryStage = new Stage();
            setupStage(userEmail);
            primaryStage.show();
        });
    }

    private void setupStage(String userEmail) {
        VBox root = new VBox();
        root.setSpacing(10);

        Label emailLabel = new Label("Account: " + userEmail);
        ListView<String> emailListView = new ListView<>();
        Button refreshButton = new Button("Aggiorna");
        Button sendButton = new Button("Nuova Email");
        Button forwardButton = new Button("Inoltra a");
        Button backButton = new Button("Torna alla selezione delle email");

        refreshButton.setOnAction(e -> refreshEmailList(emailListView));
        sendButton.setOnAction(e -> sendEmail());
        backButton.setOnAction(e -> goBackToEmailSelection());
        forwardButton.setOnAction(e -> forwardEmailTo());

        root.getChildren().addAll(emailLabel, refreshButton, emailListView, sendButton, backButton);

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("Email Client - " + userEmail);
        primaryStage.setScene(scene);
    }

    private void forwardEmailTo() {

    }

    private void goBackToEmailSelection() {     // do not touch
        if (primaryStage != null) {
            primaryStage.close(); // Chiudi la finestra del client
            // Riavvia la selezione delle email
            Server.showEmailSelection(); // Chiamata al metodo statico del Server
        }
    }

    private void refreshEmailList(ListView<String> emailListView) {
        // Codice per aggiornare la lista delle email dal server
    }

    private void sendEmail() {
        // Codice per inviare l'email al server
    }
}
