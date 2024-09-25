package com.progetto.nuovo_progetto.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.progetto.nuovo_progetto.server.controller.ServerController;
import com.progetto.nuovo_progetto.server.model.ServerModel;
import com.progetto.nuovo_progetto.common.Email;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MailServer extends Application {
    private static final int PORT = 5000;  // Porta del server
    private ServerController controller;
    private ServerModel model;
    private ExecutorService executorService;
    private ServerSocket serverSocket;  // Socket del server per fermarlo in modo sicuro

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/progetto/nuovo_progetto/server/ServerView.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        model = new ServerModel();
        controller.setModel(model);
        controller.setServer(this);  // Passa una referenza di MailServer al controller

        primaryStage.setTitle("Mail Server");
        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();
    }

    /**
     * Avvia il server di posta.
     */
    public void startServer() {
        executorService = Executors.newCachedThreadPool();  // Per gestire i thread dei client
        new Thread(this::acceptClients).start();  // Thread separato per accettare connessioni dei client
    }

    /**
     * Accetta le connessioni dai client.
     */
    private void acceptClients() {
        try {
            serverSocket = new ServerSocket(PORT);  // Apre il ServerSocket sulla porta specificata
            controller.handleServerStarted(PORT);   // Notifica al controller che il server è avviato
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();  // Attende le connessioni dei client
                executorService.submit(() -> handleClient(clientSocket));  // Gestisce ciascun client su un thread separato
            }
        } catch (IOException e) {
            if (serverSocket.isClosed()) {
                controller.handleServerStopped();  // Notifica al controller che il server è stato fermato
            } else {
                e.printStackTrace();
                model.addLogEntry("Server error: " + e.getMessage());
            }
        }
    }

    /**
     * Gestisce ciascun client connesso.
     */
    private void handleClient(Socket clientSocket) {
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

            String clientAddress = clientSocket.getInetAddress().getHostAddress();
            controller.handleClientConnection(clientAddress);  // Logga la connessione del client

            String request = (String) in.readObject();  // Legge la richiesta dal client
            switch (request) {
                case "FETCH_EMAILS":
                    handleFetchEmails(in, out);
                    break;
                case "SEND_EMAIL":
                    handleSendEmail(in);
                    break;
                default:
                    model.addLogEntry("Unknown request: " + request);
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addLogEntry("Error handling client: " + e.getMessage());
        }
    }

    /**
     * Gestisce la richiesta di FETCH_EMAILS.
     */
    private void handleFetchEmails(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        String emailAddress = (String) in.readObject();  // Legge l'indirizzo email dal client
        List<Map<String, Object>> emails = model.getEmails(emailAddress);  // Recupera le email dal modello
        out.writeObject(emails);  // Invia le email al client
        out.flush();
        model.markEmailsAsRead(emailAddress);  // Marca le email come lette
    }

    /**
     * Gestisce la richiesta di SEND_EMAIL.
     */
    private void handleSendEmail(ObjectInputStream in) throws IOException, ClassNotFoundException {
        Email email = (Email) in.readObject();
        for (String recipient : email.getRecipients()) {
            model.addEmail(
                    recipient,               // Recipient
                    email.getSender(),        // Sender
                    email.getRecipients(),    // List of recipients
                    email.getSubject(),       // Subject
                    email.getContent(),       // Content
                    email.getSentDate(),      // Sent date
                    email.isRead()            // Read status
            );
        }
        controller.handleEmailReceived(email.getSender(), String.join(", ", email.getRecipients()));
    }


    /**
     * Ferma il server di posta in modo sicuro.
     */
    public void stopServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();  // Chiude il ServerSocket per fermare il server
                controller.handleServerStopped();  // Notifica al controller che il server è fermato
                executorService.shutdown();  // Ferma i thread dei client
            }
        } catch (IOException e) {
            e.printStackTrace();
            model.addLogEntry("Error stopping server: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);  // Avvia l'applicazione JavaFX
    }
}
