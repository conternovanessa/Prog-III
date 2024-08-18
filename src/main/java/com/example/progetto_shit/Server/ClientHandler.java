package com.example.progetto_shit.Server;

import com.example.progetto_shit.Client.EmailClientManager;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private List<String> clientList;

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
            System.out.println("Email received from client: " + emailMessage);

            // Forwarding the email to the selected client
            for (String client : clientList) {
                if (client.equals(emailMessage.split(":")[0])) { // assuming email format as "recipient: message"
                    EmailClientManager clientManager = new EmailClientManager("localhost", 12346); // New port for client connection
                    clientManager.sendMessageToServer(emailMessage);
                    System.out.println("Email forwarded to: " + client);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error handling client: " + e.getMessage());
        }
    }
}
