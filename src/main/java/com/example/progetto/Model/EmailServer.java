package com.example.progetto.Model;

import com.example.progetto.Controller.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class EmailServer {
    private int port;
    private List<String> clientList;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private volatile boolean running;
    private static final Logger logger = Logger.getLogger(EmailServer.class.getName());

    public EmailServer(int port, List<String> clientList) {
        this.port = port;
        this.clientList = clientList;
        this.threadPool = Executors.newCachedThreadPool();
    }

    public void start() {
        running = true;
        try {
            serverSocket = new ServerSocket(port);
            logger.info("Email server started on port " + port);

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(clientSocket, clientList);
                    threadPool.execute(clientHandler);
                } catch (IOException e) {
                    if (running) {
                        logger.severe("Error accepting client connection: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            logger.severe("Error starting the server: " + e.getMessage());
        } finally {
            stop();
        }
    }

    public void stop() {
        running = false;
        threadPool.shutdownNow();
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                logger.severe("Error closing server socket: " + e.getMessage());
            }
        }
        logger.info("Email server stopped");
    }
}
