package com.example.progetto_shit.Server;

import com.example.progetto_shit.Server.MessageStorage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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

            Scene scene = new Scene(layout, 300, 400); // Modifica la dimensione della finestra
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

        // Mostra le e-mail ricevute
        VBox emailBox = new VBox(10);
        List<String> receivedMails = getReceivedMailsForClient(selectedClient); // Utilizza MessageStorage
        for (String email : receivedMails) {
            Label emailLabel = new Label(email); // Mostra ogni email come Label
            emailBox.getChildren().add(emailLabel);
        }

        // Usa un ScrollPane per gestire la visualizzazione delle e-mail
        ScrollPane emailScrollPane = new ScrollPane(emailBox);
        emailScrollPane.setFitToWidth(true);
        emailScrollPane.setPrefHeight(200); // Altezza fissa per la visualizzazione

        // Crea una VBox per contenere email e pulsanti
        VBox contentBox = new VBox(10);
        contentBox.getChildren().addAll(emailScrollPane, createActionButtons());

        buttonBox.getChildren().add(contentBox);
    }

    private VBox createActionButtons() {
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

        VBox buttonBox = new VBox(10);
        buttonBox.getChildren().addAll(newMailButton, receivedMailsButton, forwardButton, replyButton, backButton);
        return buttonBox;
    }

    private void handleNewMail() {
        System.out.println("Creating a new mail...");
        NewMailHandler newMailHandler = new NewMailHandler(selectedClient);
        newMailHandler.createNewMail();
    }

    private void handleReceivedMails() {
        // La logica per la visualizzazione dei messaggi verrà gestita dal client.
        System.out.println("Showing received emails...");
        updateClientInterface(); // Ricarica l'interfaccia per mostrare le e-mail aggiornate
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

    private List<String> getReceivedMailsForClient(String client) {
        // Utilizza MessageStorage per ottenere le e-mail ricevute
        return MessageStorage.getMessagesForRecipient(client);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
