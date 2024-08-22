package com.example.progetto_shit.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientController {

    @FXML
    private Label statusLabel;

    @FXML
    private VBox buttonBox;

    @FXML
    private TextArea emailContentArea;

    private List<String> clientList = new ArrayList<>();
    private String selectedClient;
    private String selectedEmail;
    private ServerController serverController; // Aggiungi una variabile per ServerController
    @FXML
    private Label serverAddressLabel;

    private String serverAddress;

    public void initialize() {
        // Questo metodo viene chiamato dopo che l'FXML è stato caricato
        // Può essere vuoto se non hai bisogno di inizializzazione aggiuntiva
    }
    // Metodo per inizializzare il controller con l'indirizzo del server
    public void initialize(String serverAddress) {
        this.serverAddress = serverAddress;
        serverAddressLabel.setText("Server Address: " + serverAddress);
    }

    // Metodo per impostare l'istanza di ServerController
    public void setServerController(ServerController serverController) {
        this.serverController = serverController;
    }

    // Questo metodo viene chiamato per connettere il client al server e per aggiornare la UI
    public void connectToServer() {
        // Logica di connessione al server
        System.out.println("Connecting to server...");
    }

    // Questo metodo aggiorna l'interfaccia utente in base ai dati ricevuti dal server
    public void updateUI(String data) {
        // Aggiorna la UI con i dati del server
        emailContentArea.setText(data);
    }

    @FXML
    private void handleSendEmail() {
        // Logica per inviare una email al server
        System.out.println("Sending email...");
    }

    @FXML
    private void handleRefreshEmails() {
        // Logica per aggiornare la lista di email dal server
        System.out.println("Refreshing emails...");
        updateClientInterface();
    }

    // Metodo per caricare i client da file
    public void loadClientsFromFile(String filePath) {
        buttonBox.getChildren().clear();

        Button stopButton = new Button("Stop Server");
        buttonBox.getChildren().addAll(statusLabel, stopButton);

        stopButton.setOnAction(event -> {
            if (serverController != null) {
                serverController.stopServer(); // Usa l'istanza di ServerController
            }
        });

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

    // Metodo per ottenere le email ricevute per un client specifico
    private List<String> getReceivedMailsForClient(String client) {
        // Logica per ottenere le email ricevute dal server per il client selezionato
        // Placeholder per il contenuto reale
        List<String> emails = new ArrayList<>();
        emails.add("From: example1@example.com\nSubject: Hello World\nThis is a test email.");
        emails.add("From: example2@example.com\nSubject: Another Email\nHere is some more content.");
        return emails;
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

    @FXML
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

    @FXML
    private void handleForward() {
        System.out.println("Inoltrando l'email...");
        if (selectedEmail != null) {
            ForwardHandler forwardHandler = new ForwardHandler(selectedClient);
            forwardHandler.forwardEmail(selectedEmail);
        } else {
            showAlert("Selezione Mancante", "Per favore seleziona un'email da inoltrare.");
        }
    }

    @FXML
    private void handleBack() {
        System.out.println("Tornando indietro...");
        // Implementa la logica per tornare alla schermata precedente
    }

    // Metodo di utilità per mostrare avvisi
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
