package com.example.progetto_shit.Server;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
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
    private TextArea messageArea;

    // Specifica il percorso del file email.txt direttamente nel codice
    private static final String FILE_PATH = "src/main/java/com/example/progetto_shit/email.txt";

    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setTitle("Mail Server");

            // Setup GUI elements
            statusLabel = new Label("Server Status: Starting...");
            layout = new BorderPane(); // Use BorderPane to layout different sections
            buttonBox = new VBox(10); // VBox for the buttons
            Button stopButton = new Button("Stop Server");
            messageArea = new TextArea(); // Area to display messages
            messageArea.setEditable(false); // Make it non-editable

            // Set up layout
            buttonBox.getChildren().addAll(statusLabel, stopButton);
            layout.setTop(buttonBox);
            layout.setCenter(messageArea);

            // Show the scene
            Scene scene = new Scene(layout, 600, 400); // Adjust size as needed
            primaryStage.setScene(scene);
            primaryStage.show();

            // Load clients from the predefined file path and create buttons
            loadClientsFromFile(FILE_PATH);

            // Start server
            ServerController.startServer(statusLabel, clientList);

            // Stop server logic
            stopButton.setOnAction(event -> ServerController.stopServer(statusLabel));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to load clients from the specified file path and create buttons
    private void loadClientsFromFile(String filePath) {
        buttonBox.getChildren().clear(); // Clear previous buttons
        buttonBox.getChildren().addAll(statusLabel, new Button("Stop Server")); // Add statusLabel and stopButton

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String client = line.trim();
                clientList.add(client);

                // Create a button for each client
                Button clientButton = new Button("Connect to " + client);
                clientButton.setOnAction(event -> {
                    selectedClient = client;
                    updateClientInterface(); // Update the interface with client options
                });

                // Add button to the VBox
                buttonBox.getChildren().add(clientButton);
            }

            // Check if clients were added correctly
            if (clientList.isEmpty()) {
                System.out.println("No clients found in the specified file.");
            } else {
                System.out.println("Clients loaded: " + clientList);
            }

        } catch (IOException e) {
            System.err.println("Error reading the specified file: " + e.getMessage());
        }
    }

    // Method to update the interface with client options
    private void updateClientInterface() {
        buttonBox.getChildren().clear(); // Clear previous buttons

        Button newMailButton = new Button("Nuova Mail");
        Button receivedMailsButton = new Button("Email Ricevute");
        Button forwardButton = new Button("Inoltra");
        Button replyButton = new Button("Rispondi");
        Button backButton = new Button("Torna Indietro");

        // Set actions for the buttons
        newMailButton.setOnAction(event -> handleNewMail());
        receivedMailsButton.setOnAction(event -> handleReceivedMails());
        forwardButton.setOnAction(event -> handleForward());
        replyButton.setOnAction(event -> handleReply());
        backButton.setOnAction(event -> handleBack());

        // Add buttons to the VBox
        buttonBox.getChildren().addAll(newMailButton, receivedMailsButton, forwardButton, replyButton, backButton);
        layout.setCenter(messageArea); // Ensure messageArea is still shown
    }

    // Placeholder methods for button actions
    private void handleNewMail() {
        System.out.println("Creating a new mail...");
        NewMailHandler newMailHandler = new NewMailHandler(selectedClient);
        newMailHandler.createNewMail();
    }

    private void handleReceivedMails() {
        System.out.println("Showing received emails...");
        ReceivedMailsHandler receivedMailsHandler = new ReceivedMailsHandler(selectedClient);
        String emails = receivedMailsHandler.getReceivedMails();
        messageArea.setText(emails); // Display emails in the messageArea
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
        // Reload the list of clients
        loadClientsFromFile(FILE_PATH);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
