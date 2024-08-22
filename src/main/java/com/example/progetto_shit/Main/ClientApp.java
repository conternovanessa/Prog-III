package com.example.progetto_shit.Main;

import com.example.progetto_shit.Model.MessageStorage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class ClientApp extends Application {

    private String clientAddress;
    private TextArea emailArea;

    public ClientApp(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Client Mail Viewer - " + clientAddress);

        VBox layout = new VBox(10);
        Label clientLabel = new Label("Client: " + clientAddress);
        emailArea = new TextArea();
        emailArea.setEditable(false);  // Impedisce la modifica del testo
        Button refreshButton = new Button("Aggiorna");

        refreshButton.setOnAction(event -> displayReceivedMails());

        layout.getChildren().addAll(clientLabel, emailArea, refreshButton);

        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Mostra i messaggi all'avvio
        displayReceivedMails();
    }

    private void displayReceivedMails() {
        // Recupera i messaggi dal MessageStorage
        List<String> emails = MessageStorage.getMessagesForRecipient(clientAddress);

        if (emails.isEmpty()) {
            emailArea.setText("Non ci sono email per " + clientAddress);
        } else {
            StringBuilder emailContent = new StringBuilder();
            for (String email : emails) {
                emailContent.append(email).append("\n\n");
            }
            emailArea.setText(emailContent.toString());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
