
package com.example.progetto.Model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

// Import statements remain the same

public class EmailClientManager {

    private String serverAddress;
    private int serverPort;

    public EmailClientManager(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void sendMessageToServer(Object message) throws IOException {
        try (Socket socket = new Socket(serverAddress, serverPort);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            out.writeObject(message);
            out.flush();
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + serverAddress);
        } catch (IOException e) {
            System.err.println("IOException while communicating with server: " + e.getMessage());
            throw e;
        }
    }

    public void receiveMessages() {
        new Thread(() -> {
            try (Socket socket = new Socket(serverAddress, serverPort);
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                while (true) {
                    String message = (String) in.readObject();
                    System.out.println("New message from server: " + message);
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error receiving messages from server: " + e.getMessage());
            }
        }).start();
    }
}
