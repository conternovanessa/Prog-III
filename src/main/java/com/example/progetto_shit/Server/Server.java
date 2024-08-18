package com.example.progetto_shit.Server;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Server extends Application {

    private Label statusLabel;
    private List<String> clientList = new ArrayList<>();
    private String selectedClient = null;
    private BorderPane layout;
    private VBox buttonBox;

    // Specifica il percorso del file email.txt direttamente nel codice
    private static final String FILE_PATH = "src/main/java/com/example/progetto_shit/email.txt";

    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setTitle("Mail Server");

            // Setup GUI elements
            statusLabel = new Label("Server Status: Running...");
            layout = new BorderPane();
            buttonBox = new VBox(10);
            Button stopButton = new Button("Stop Server");

            // Imposta il layout iniziale
            buttonBox.getChildren().addAll(statusLabel, stopButton);
            layout.setCenter(buttonBox);

            Scene scene = new Scene(layout, 300, 200); // Modifica la dimensione della finestra
            primaryStage.setScene(scene);
            primaryStage.show();

            // Load clients and start server
            loadClientsFromFile(FILE_PATH);
            ServerController.startServer(statusLabel, clientList);

            stopButton.setOnAction(event -> ServerController.stopServer(statusLabel));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Metodo per caricare i client da file
    private void loadClientsFromFile(String filePath) {
        // Pulizia del buttonBox e ripristino del pulsante "Stop Server"
        buttonBox.getChildren().clear();
        Button stopButton = new Button("Stop Server");
        buttonBox.getChildren().addAll(statusLabel, stopButton);

        stopButton.setOnAction(event -> ServerController.stopServer(statusLabel));

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String client = line.trim();
                clientList.add(client);

                Button clientButton = new Button("Connect to " + client);
                clientButton.setOnAction(event -> {
                    selectedClient = client;
                    updateClientInterface();
                });

                buttonBox.getChildren().add(clientButton);
            }

        } catch (IOException e) {
            System.err.println("Error reading the specified file: " + e.getMessage());
        }
    }

    private void updateClientInterface() {
        // Pulisci il contenuto attuale
        buttonBox.getChildren().clear();

        Button newMailButton = new Button("Nuova Mail");
        Button receivedMailsButton = new Button("Aggiorna");
        Button forwardButton = new Button("Inoltra");
        Button replyButton = new Button("Rispondi");
        Button backButton = new Button("Torna Indietro");

        newMailButton.setOnAction(event -> handleNewMail());
        receivedMailsButton.setOnAction(event -> handleReceivedMails());
        forwardButton.setOnAction(event -> handleForward());
        replyButton.setOnAction(event -> handleReply());
        backButton.setOnAction(event -> handleBack());

        buttonBox.getChildren().addAll(newMailButton, receivedMailsButton, forwardButton, replyButton, backButton);
    }

    private void handleNewMail() {
        System.out.println("Creating a new mail...");
        NewMailHandler newMailHandler = new NewMailHandler(selectedClient);
        newMailHandler.createNewMail();
    }

    private void handleReceivedMails() {
        System.out.println("Showing received emails...");
        List<String> emails = MessageStorage.getMessagesForRecipient(selectedClient);
        if (emails.isEmpty()) {
            System.out.println("Non ci sono email per " + selectedClient);
        } else {
            for (String email : emails) {
                System.out.println(email);
            }
        }
    }

    private void handleForward() {
        System.out.println("Forwarding email...");
        ForwardHandler forwardHandler = new ForwardHandler(selectedClient);
        forwardHandler.forwardEmail();
    }

    private void handleReply() {
        System.out.println("Replying to email...");
        ReplyHandler replyHandler = new ReplyHandler(selectedClient);
        replyHandler.replyToEmail();
    }

    private void handleBack() {
        // Torna alla schermata iniziale ripristinando i client e il pulsante Stop Server
        loadClientsFromFile(FILE_PATH);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
