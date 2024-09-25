package com.progetto.nuovo_progetto.server.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import com.progetto.nuovo_progetto.server.MailServer;
import com.progetto.nuovo_progetto.server.model.ServerModel;

public class ServerController {

    @FXML
    private ListView<String> logListView;
    @FXML
    private Button stopButton;
    @FXML
    private Button startButton;

    private ServerModel model;
    private MailServer server;

    public void setModel(ServerModel model) {
        this.model = model;
        logListView.setItems(model.getLogEntries());
    }

    public void setServer(MailServer server) {
        this.server = server;
    }

    @FXML
    private void handleStartServer() {
        server.startServer(); // Avvia il server
        addLogEntry("Server starting...");
        startButton.setDisable(true);  // Disabilita il pulsante "Start"
        stopButton.setDisable(false);  // Abilita il pulsante "Stop"
    }

    @FXML
    private void handleStopServer() {
        server.stopServer();  // Ferma il server
        addLogEntry("Server stopping...");
        stopButton.setDisable(true);   // Disabilita il pulsante "Stop"
        startButton.setDisable(false); // Abilita il pulsante "Start"
    }

    public void handleServerStarted(int port) {
        addLogEntry("Server started on port " + port);
    }

    public void handleServerStopped() {
        addLogEntry("Server stopped.");
        Platform.runLater(() -> {
            startButton.setDisable(false); // Riabilita "Start"
            stopButton.setDisable(true);   // Disabilita "Stop"
        });
    }

    public void handleClientConnection(String clientAddress) {
        addLogEntry("New connection from: " + clientAddress);
    }

    public void handleEmailReceived(String sender, String recipient) {
        addLogEntry("Email received from " + sender + " to " + recipient);
    }

    private void addLogEntry(String entry) {
        Platform.runLater(() -> model.addLogEntry(entry));
    }
}
