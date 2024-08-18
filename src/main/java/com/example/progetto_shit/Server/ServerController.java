package com.example.progetto_shit.Server;

import com.example.progetto_shit.Client.EmailClientManager;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ServerController {

    private static boolean serverRunning = false;
    private static ServerSocket serverSocket;
    private static final int PORT = 12345;

    public static void startServer(Label statusLabel, List<String> clientList) {
        if (!serverRunning) {
            serverRunning = true;
            statusLabel.setText("Server Status: Running");

            System.out.println("Server started.");
            System.out.println("Clients available: " + clientList);

            new Thread(() -> {
                try {
                    serverSocket = new ServerSocket(PORT);
                    while (serverRunning) {
                        Socket clientSocket = serverSocket.accept();
                        new ClientHandler(clientSocket, clientList).start();
                    }
                } catch (IOException e) {
                    System.err.println("Error in server socket: " + e.getMessage());
                }
            }).start();
        }
    }

    public static void stopServer(Label statusLabel) {
        if (serverRunning) {
            serverRunning = false;
            statusLabel.setText("Server Status: Stopped");

            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing server socket: " + e.getMessage());
            }

            System.out.println("Server stopped.");
        }
    }
}
