package com.progetto.nuovo_progetto.server.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import com.progetto.nuovo_progetto.server.model.ServerModel;

public class ServerController {
    @FXML
    private ListView<String> logListView;

    private ServerModel model;

    public void setModel(ServerModel model) {
        this.model = model;
        logListView.setItems(model.getLogEntries());
    }

    public void handleServerStarted(int port) {
        addLogEntry("Server started on port " + port);
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
