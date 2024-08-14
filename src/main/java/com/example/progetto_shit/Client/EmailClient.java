package com.example.progetto_shit.Client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class EmailClient extends Application implements Runnable {
    private String serverAddress;
    private int serverPort;
    private String email;

    public EmailClient() {
        // Costruttore vuoto richiesto da JavaFX
    }

    public EmailClient(String serverAddress, int serverPort, String email) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.email = email;
    }

    @Override
    public void run() {
        // Lancia l'interfaccia JavaFX
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        // Creazione dell'interfaccia grafica del client
        VBox root = new VBox();
        root.setSpacing(10);

        Label emailLabel = new Label("Email: " + email);
        Button sendButton = new Button("Invia Email");

        sendButton.setOnAction(e -> sendEmail());

        root.getChildren().addAll(emailLabel, sendButton);

        Scene scene = new Scene(root, 300, 150);
        primaryStage.setTitle("Client Email");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void sendEmail() {
        try (Socket socket = new Socket(serverAddress, serverPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Invia l'email al server
            out.println(email);
            System.out.println("Inviato: " + email);

            // Leggi la risposta dal server
            String response = in.readLine();
            System.out.println("Risposta dal server: " + response);
        } catch (IOException e) {
            System.err.println("Errore di connessione al server: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Argomenti per l'avvio del client
        if (args.length < 3) {
            System.err.println("Uso: java EmailClient <indirizzo-server> <porta> <email>");
            System.exit(1);
        }

        String serverAddress = args[0];
        int serverPort = Integer.parseInt(args[1]);
        String email = args[2];

        EmailClient client = new EmailClient(serverAddress, serverPort, email);
        new Thread(client).start();
    }
}
