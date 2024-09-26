package com.progetto.nuovo_progetto.server.controller;

import com.progetto.nuovo_progetto.server.MailServer;
import com.progetto.nuovo_progetto.server.model.ServerModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

public class ServerController {
    @FXML
    private Button toggleServerButton;
    @FXML
    private ListView<String> logListView;

    private ServerModel model;
    private MailServer server;
    private boolean isServerRunning = false;

    public void setModel(ServerModel model) {
        this.model = model;
        logListView.setItems(model.getLogEntries());
    }

    public void setServer(MailServer server) {
        this.server = server;
    }

    @FXML
    private void handleToggleServer() {
        if (isServerRunning) {
            server.stopServer();
            isServerRunning = false;
            toggleServerButton.setText("Accendi Server");
        } else {
            server.startServer();
            isServerRunning = true;
            toggleServerButton.setText("Spegni Server");
        }
    }

    public void handleServerStarted(int port) {
        isServerRunning = true;
        toggleServerButton.setText("Spegni Server");
        model.addLogEntry("Server started on port " + port);
    }

    public void handleServerStopped() {
        isServerRunning = false;
        toggleServerButton.setText("Accendi Server");
        model.addLogEntry("Server stopped");
    }

    public void handleClientConnection(String clientAddress) {
        model.addLogEntry("Client connected: " + clientAddress);
    }

    public void handleEmailReceived(String sender, String recipients) {
        model.addLogEntry("Email received from " + sender + " to " + recipients);
    }
}



