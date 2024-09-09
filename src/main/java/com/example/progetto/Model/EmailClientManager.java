package com.example.progetto.Model;

import com.example.progetto.Util.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class EmailClientManager {
    private static final int DEFAULT_PORT = 55555;

    private String serverAddress;
    private int serverPort;

    public EmailClientManager(String serverAddress) {
        this(serverAddress, DEFAULT_PORT);
    }

    public EmailClientManager(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void sendMessageToServer(Object message) throws IOException {
        Logger.log("Attempting to send message to server: " + serverAddress + ":" + serverPort);
        try (Socket socket = new Socket(serverAddress, serverPort);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
            out.writeObject(message);
            out.flush();
            Logger.log("Message sent successfully");
        } catch (UnknownHostException e) {
            Logger.log("Unknown host: " + serverAddress);
            throw e;
        } catch (IOException e) {
            Logger.log("IOException while communicating with server: " + e.getMessage());
            throw e;
        }
    }

    public void receiveMessages() {
        Logger.log("Starting message receiver thread");
        new Thread(() -> {
            while (true) {
                try (Socket socket = new Socket(serverAddress, serverPort);
                     ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                    String message = (String) in.readObject();
                    Logger.log("New message received from server: " + message);

                    // Estrai il destinatario dal messaggio
                    String recipient = extractRecipient(message);

                    // Aggiungi il messaggio al MessageStorage
                    MessageStorage.addMessage(recipient, message);

                } catch (IOException | ClassNotFoundException e) {
                    Logger.log("Error receiving messages from server: " + e.getMessage());
                    try {
                        Thread.sleep(5000); // Attendi 5 secondi prima di riprovare
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }).start();
    }

    private String extractRecipient(String message) {
        // Implementa la logica per estrarre il destinatario dal messaggio
        // Ad esempio, se il messaggio è nel formato "To: recipient@example.com\n..."
        String[] lines = message.split("\n");
        for (String line : lines) {
            if (line.startsWith("To:")) {
                return line.substring(3).trim();
            }
        }
        return "";
    }
}