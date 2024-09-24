package com.progetto.nuovo_progetto.client.controller;

import com.progetto.nuovo_progetto.client.model.ClientModel;
import com.progetto.nuovo_progetto.common.Email;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class ClientController {
    @FXML private TableView<Email> emailTableView;
    @FXML private TableColumn<Email, String> senderColumn;
    @FXML private TableColumn<Email, String> subjectColumn;
    @FXML private TableColumn<Email, String> dateColumn;

    // Riferimenti ai campi della sezione di composizione
    @FXML private VBox composeSection;
    @FXML private TextField toField;
    @FXML private TextField subjectField;
    @FXML private TextArea bodyArea;

    private ClientModel model;

    public void initialize() {
        // Configurazione delle colonne della TableView
        senderColumn.setCellValueFactory(new PropertyValueFactory<>("sender"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("sentDate"));
    }

    public void setModel(ClientModel model) {
        this.model = model;
        emailTableView.setItems(model.getInbox());  // Popola la tabella con le email dell'inbox
    }

    @FXML
    private void handleCompose() {
        // Mostra la sezione per comporre una nuova email
        composeSection.setVisible(true);
    }

    @FXML
    private void handleSend() {
        // Creazione dell'oggetto email
        Email email = new Email();
        email.setSender(model.getEmailAddress());
        email.setRecipients(Arrays.asList(toField.getText().split(",")));
        email.setSubject(subjectField.getText());
        email.setContent(bodyArea.getText());
        email.setSentDate(LocalDateTime.now());

        // Invia l'email
        sendEmail(email);

        // Pulisci i campi e nascondi la sezione di composizione
        clearComposeFields();
        composeSection.setVisible(false);
    }

    private void sendEmail(Email email) {
        // Logica per inviare l'email al server
        try (Socket socket = new Socket("localhost", 5000);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            out.writeObject("SEND_EMAIL");
            out.writeObject(email);
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
            // Gestisci l'errore con un messaggio all'utente
        }
    }

    @FXML
    private void handleCancel() {
        // Nasconde la sezione di composizione e pulisce i campi
        clearComposeFields();
        composeSection.setVisible(false);
    }

    private void clearComposeFields() {
        toField.clear();
        subjectField.clear();
        bodyArea.clear();
    }

    @FXML
    private void handleRefresh() {
        // Aggiorna la lista delle email
        fetchEmails();
    }

    private void fetchEmails() {
        // Logica per ottenere le email dal server
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
            // Gestisci l'errore con un messaggio all'utente
        }
    }

    @FXML
    private void handleViewEmail() {
        // Logica per visualizzare un'email selezionata
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
                // Gestisci l'errore con un messaggio all'utente
            }
        }
    }
}
