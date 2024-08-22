package com.example.progetto_shit.Main;

import com.example.progetto_shit.Controller.ForwardHandler;
import com.example.progetto_shit.Controller.NewMailHandler;
import com.example.progetto_shit.Controller.ReplyHandler;
import com.example.progetto_shit.Controller.ServerController;
import com.example.progetto_shit.Model.MessageStorage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    private String selectedEmail = null;

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
        List<String> receivedMails = getReceivedMailsForClient(selectedClient);
        for (String email : receivedMails) {
            String[] emailLines = email.split("\n", 3); // Splitta le prime due righe (Mittente e Oggetto)

            if (emailLines.length >= 2) {
                String sender = emailLines[0].replace("From: ", "");
                String subject = emailLines[1].replace("Subject: ", "");
                String buttonText = sender + " - " + subject;

                Button emailButton = new Button(buttonText);
                emailButton.setOnAction(event -> {
                    selectedEmail = email; // Memorizza l'email selezionata
                    showEmailDetailView(email); // Mostra l'interfaccia dettagliata
                });

                emailBox.getChildren().add(emailButton);
            }
        }

        // Usa un ScrollPane per gestire la visualizzazione delle e-mail
        ScrollPane emailScrollPane = new ScrollPane(emailBox);
        emailScrollPane.setFitToWidth(true);
        emailScrollPane.setPrefHeight(200); // Altezza fissa per la visualizzazione

        VBox contentBox = new VBox(10);
        contentBox.getChildren().addAll(emailScrollPane, createActionButtons());

        buttonBox.getChildren().add(contentBox);
    }


    // Metodo per visualizzare i dettagli dell'email selezionata
    private void showEmailDetailView(String email) {
        // Estrae i dettagli dell'email
        String[] emailLines = email.split("\n", 3);
        String sender = emailLines.length > 0 ? emailLines[0].replace("From: ", "") : "Unknown Sender";
        String subject = emailLines.length > 1 ? emailLines[1].replace("Subject: ", "") : "No Subject";
        String body = emailLines.length > 2 ? emailLines[2] : "No Content";

        // Crea una nuova finestra per i dettagli dell'email
        Stage emailDetailStage = new Stage();
        emailDetailStage.setTitle("Email Details");

        // Crea i controlli per visualizzare i dettagli
        Label senderLabel = new Label("From: " + sender);
        Label subjectLabel = new Label("Subject: " + subject);
        TextArea bodyArea = new TextArea(body);
        bodyArea.setWrapText(true);
        bodyArea.setEditable(false);

        // Pulsanti per rispondere e inoltrare
        Button replyButton = new Button("Reply");
        replyButton.setOnAction(event -> handleReply());
        Button forwardButton = new Button("Forward");
        forwardButton.setOnAction(event -> handleForward());

        VBox detailBox = new VBox(10, senderLabel, subjectLabel, bodyArea, replyButton, forwardButton);
        detailBox.setPrefSize(400, 300);

        Scene detailScene = new Scene(detailBox);
        emailDetailStage.setScene(detailScene);
        emailDetailStage.show();
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
        System.out.println("Showing received emails...");
        updateClientInterface();
    }

    private void handleReply() {
        System.out.println("Rispondendo all'email...");
        if (selectedEmail != null) {
            // Estrae il mittente dall'email selezionata
            String[] emailLines = selectedEmail.split("\n", 3);
            String sender = emailLines.length > 0 ? emailLines[0].replace("From: ", "") : "Unknown Sender";

            // Inizializza il ReplyHandler con l'indirizzo del mittente e del client corrente
            ReplyHandler replyHandler = new ReplyHandler(sender, selectedClient);
            replyHandler.replyToEmail();
        } else {
            showAlert("Selezione Mancante", "Per favore seleziona un'email a cui rispondere.");
        }
    }


    private void handleForward() {
        System.out.println("Inoltrando l'email...");
        if (selectedEmail != null) {
            ForwardHandler forwardHandler = new ForwardHandler(selectedClient);
            //forwardHandler.forwardEmail(selectedEmail); // Implementa il metodo nel ForwardHandler
        } else {
            showAlert("Selezione Mancante", "Per favore seleziona un'email da inoltrare.");
        }
    }

    // Metodo di utilità per mostrare avvisi
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void handleBack() {
        loadClientsFromFile(FILE_PATH);
    }

    private List<String> getReceivedMailsForClient(String client) {
        return MessageStorage.getMessagesForRecipient(client);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
