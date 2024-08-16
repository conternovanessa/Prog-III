
package com.example.progetto_shit.Client;

import com.example.progetto_shit.Server.Server;
import javafx.application.Platform; // Aggiungi questa importazione
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class EmailClientManager {
    private static EmailClientManager instance;
    private Stage primaryStage;
    private String email;

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

        root.getChildren().addAll(emailLabel, refreshButton, emailListView, sendButton, forwardButton, backButton);


        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("Email Client - " + userEmail);
        primaryStage.setScene(scene);
    }

    private void forwardEmailTo() {
        // Ottieni l'email selezionata dall'interfaccia utente (esempio)
        String selectedEmail = "Email di esempio da inoltrare"; // Da sostituire con la logica per ottenere l'email selezionata
        List<String> recipients = List.of("client1@example.com", "client2@example.com"); // Lista di destinatari

        // Inoltra l'email utilizzando la classe EmailForwarder
        EmailForwarder forwarder = new EmailForwarder("localhost", 8080); // Assumi che il server sia su localhost e porta 8080
        forwarder.forwardEmail(selectedEmail, recipients);
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
            Stage composeStage = new Stage();
            VBox composeRoot = new VBox();
            composeRoot.setSpacing(10);

            // Creazione dei campi di input
            TextField recipientField = new TextField();
            recipientField.setPromptText("Destinatario");
            TextField subjectField = new TextField();
            subjectField.setPromptText("Oggetto");
            TextArea bodyArea = new TextArea();
            bodyArea.setPromptText("Corpo del messaggio");

            Button sendButton = new Button("Invia");
            Label errorLabel = new Label();
            errorLabel.setStyle("-fx-text-fill: red;");

            sendButton.setOnAction(e -> {
                String recipient = recipientField.getText();
                String subject = subjectField.getText();
                String body = bodyArea.getText();

                // Validazione del destinatario
                if (!recipient.equals(this.email)) {
                    errorLabel.setText("Errore: Email inviata dal client sbagliato!");
                } else {
                    String serverAddress="localhost";
                    int serverPort=55555;
                    try (Socket socket = new Socket(serverAddress, serverPort);
                         PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                         BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                        // Invia l'email al server
                        out.println("To: " + recipient);
                        out.println("Subject: " + subject);
                        out.println("Body: " + body);
                        out.println("EndOfEmail"); // Simbolo di fine email

                        System.out.println("Inviato: " + body);

                        // Leggi la risposta dal server
                        String response = in.readLine();
                        System.out.println("Risposta dal server: " + response);

                        if (response.equals("OK")) {
                            composeStage.close(); // Chiudi la finestra solo se l'invio ha successo
                        } else {
                            errorLabel.setText("Errore: Il server ha risposto con un errore.");
                        }
                    } catch (IOException ex) {
                        errorLabel.setText("Errore di connessione al server: " + ex.getMessage());
                    }
                }
            });

            composeRoot.getChildren().addAll(new Label("Componi Email"), recipientField, subjectField, bodyArea, sendButton, errorLabel);

            Scene scene = new Scene(composeRoot, 400, 300);
            composeStage.setTitle("Componi Nuova Email");
            composeStage.setScene(scene);
            composeStage.show();

    }
}
