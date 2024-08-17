
package com.example.progetto_shit.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

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
            throw e;  // Re-throw the exception after logging it
        }
    }

    public Object receiveMessageFromServer() throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket(serverAddress, serverPort);
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            return in.readObject();
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + serverAddress);
            throw e;
        } catch (IOException e) {
            System.err.println("IOException while receiving from server: " + e.getMessage());
            throw e;
        }
    }
}