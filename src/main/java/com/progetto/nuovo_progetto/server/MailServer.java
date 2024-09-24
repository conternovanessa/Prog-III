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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MailServer extends Application {
    private static final int PORT = 5000;
    private ServerController controller;
    private ServerModel model;
    private ExecutorService executorService;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/progetto/nuovo_progetto/server/ServerView.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        model = new ServerModel();
        controller.setModel(model);

        primaryStage.setTitle("Mail Server");
        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();

        startServer();
    }

    private void startServer() {
        executorService = Executors.newCachedThreadPool();
        new Thread(this::acceptClients).start();
    }

    private void acceptClients() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            controller.handleServerStarted(PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                executorService.submit(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
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
                    handleSendEmail(in);
                    break;
                default:
                    System.out.println("Unknown request: " + request);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleFetchEmails(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        String emailAddress = (String) in.readObject();
        List<Email> emails = model.getEmails(emailAddress);
        out.writeObject(emails);
        out.flush();
        model.clearEmails(emailAddress); // Clear emails after sending them to the client
    }

    private void handleSendEmail(ObjectInputStream in) throws IOException, ClassNotFoundException {
        Email email = (Email) in.readObject();
        for (String recipient : email.getRecipients()) {
            model.addEmail(recipient, email);
        }
        controller.handleEmailReceived(email.getSender(), String.join(", ", email.getRecipients()));
    }

    public static void main(String[] args) {
        launch(args);
    }
}