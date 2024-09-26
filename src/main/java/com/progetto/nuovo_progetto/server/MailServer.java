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

        startServer();
    }

    public void startServer() {
        executorService = Executors.newCachedThreadPool();
        new Thread(this::acceptClients).start();
        controller.handleServerStarted(PORT);  // Notifica al controller che il server è stato avviato
    }

    private void acceptClients() {
        try {
            serverSocket = new ServerSocket(PORT);
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                executorService.submit(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            if (serverSocket.isClosed()) {
                controller.handleServerStopped();
            } else {
                e.printStackTrace();
                model.addLogEntry("Server error: " + e.getMessage());
            }
        }
    }

    private void handleClient(Socket clientSocket) {
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

            String clientAddress = clientSocket.getInetAddress().getHostAddress();
            controller.handleClientConnection(clientAddress);

            String request = (String) in.readObject();
            switch (request) {
                case "FETCH_EMAILS":
                    handleFetchEmails(in, out);
                    break;
                case "SEND_EMAIL":
                    handleSendEmail(in, out, clientSocket);
                    break;
                default:
                    model.addLogEntry("Unknown request: " + request);
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addLogEntry("Error handling client: " + e.getMessage());
        }
    }

    private void handleFetchEmails(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        String emailAddress = (String) in.readObject();
        List<Map<String, Object>> emails = model.getEmails(emailAddress);
        out.writeObject(emails);
        out.flush();
        model.markEmailsAsRead(emailAddress);
    }

    private void handleSendEmail(ObjectInputStream in, ObjectOutputStream out, Socket clientSocket) throws IOException, ClassNotFoundException {
        Email email = (Email) in.readObject();
        System.out.println("Received email: " + email);
        boolean allRecipientsValid = true;
        for (String recipient : email.getRecipients()) {
            if (!isValidRecipient(recipient)) {
                allRecipientsValid = false;
                break;
            }
        }

        if (allRecipientsValid) {
            for (String recipient : email.getRecipients()) {
                model.addEmail(
                        recipient,
                        email.getSender(),
                        email.getRecipients(),
                        email.getSubject(),
                        email.getContent(),
                        email.getSentDate(),
                        email.isRead()
                );
                System.out.println("Email saved for recipient: " + recipient);
            }
            controller.handleEmailReceived(email.getSender(), String.join(", ", email.getRecipients()));
            out.writeObject("SUCCESS: Email inviata con successo");
        } else {
            out.writeObject("ERROR: Uno o più destinatari non sono validi");
        }
        out.flush();
    }
    private boolean isValidRecipient(String email) {
        return model.isValidEmail(email);
    }

    @Override
    public void stop() throws Exception {
        stopServer();
        super.stop();
    }

    public void stopServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                controller.handleServerStopped();
                executorService.shutdown();
            }
        } catch (IOException e) {
            e.printStackTrace();
            model.addLogEntry("Error stopping server: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
