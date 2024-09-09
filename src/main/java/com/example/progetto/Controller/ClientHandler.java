package com.example.progetto.Controller;

import com.example.progetto.Model.EmailClientManager;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.logging.Logger;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private List<String> clientList;
    private static final int SERVER_PORT = 55555; // Usa la stessa porta del server
    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());

    public ClientHandler(Socket socket, List<String> clients) {
        this.clientSocket = socket;
        this.clientList = clients;
    }

    @Override
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

            // Reading the email object from the client
            String emailMessage = (String) in.readObject();

            // Parsing the email message to extract sender and recipient
            String[] emailParts = emailMessage.split("\n");
            String sender = "Unknown";
            String recipient = "Unknown";

            for (String part : emailParts) {
                if (part.startsWith("From: ")) {
                    sender = part.substring(6).trim();
                } else if (part.startsWith("To: ")) {
                    recipient = part.substring(4).trim();
                }
            }

            logger.info("Nuova mail da " + sender + " a " + recipient);

            // Forwarding the email to the selected client
            for (String client : clientList) {
                if (client.equals(recipient)) {
                    EmailClientManager clientManager = new EmailClientManager("localhost", SERVER_PORT);
                    clientManager.sendMessageToServer(emailMessage);
                    logger.info("Email inoltrata a: " + recipient);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.severe("Errore nella gestione del client: " + e.getMessage());
        }
    }
}