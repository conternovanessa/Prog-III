package com.progetto.nuovo_progetto.client.controller;

import com.progetto.nuovo_progetto.client.model.ClientModel;
import com.progetto.nuovo_progetto.common.Email;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ClientController {
    @FXML private TableView<Email> emailTableView;
    @FXML private TableColumn<Email, String> senderColumn;
    @FXML private TableColumn<Email, String> subjectColumn;
    @FXML private TableColumn<Email, String> dateColumn;

    private ClientModel model;

    public void initialize() {
        senderColumn.setCellValueFactory(new PropertyValueFactory<>("sender"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("sentDate"));
    }

    public void setModel(ClientModel model) {
        this.model = model;
        emailTableView.setItems(model.getInbox());
    }

    @FXML
    private void handleCompose() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/progetto/nuovo_progetto/client/ComposeView.fxml"));
            Parent root = loader.load();
            ComposeController composeController = loader.getController();
            composeController.setModel(model);

            Stage stage = new Stage();
            stage.setTitle("Compose Email");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            // Show an error dialog
        }
    }

    @FXML
    private void handleRefresh() {
        fetchEmails();
    }

    private void fetchEmails() {
        try (Socket socket = new Socket("localhost", 5000);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject("FETCH_EMAILS");
            out.writeObject(model.getEmailAddress());
            out.flush();

            List<Email> newEmails = (List<Email>) in.readObject();
            for (Email email : newEmails) {
                model.addEmail(email);
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            // Gestisci l'errore, magari mostrando un messaggio all'utente
        }
    }
    @FXML
    private void handleViewEmail() {
        Email selectedEmail = emailTableView.getSelectionModel().getSelectedItem();
        if (selectedEmail != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/progetto/nuovo_progetto/client/EmailView.fxml"));
                Parent root = loader.load();
                EmailViewController emailViewController = loader.getController();
                emailViewController.setEmail(selectedEmail);

                Stage stage = new Stage();
                stage.setTitle("View Email");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                // Show an error dialog
            }
        }
    }
}