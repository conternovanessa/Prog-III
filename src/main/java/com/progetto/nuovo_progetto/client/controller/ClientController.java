package com.progetto.nuovo_progetto.client.controller;

import com.progetto.nuovo_progetto.client.model.ClientModel;
import com.progetto.nuovo_progetto.common.Email;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class ClientController {
    @FXML private TableView<Email> emailTableView;
    @FXML private TableColumn<Email, String> senderColumn;
    @FXML private TableColumn<Email, String> subjectColumn;
    @FXML private TableColumn<Email, String> dateColumn;

    @FXML private VBox rightSection;
    @FXML private VBox viewSection;
    @FXML private VBox composeSection;

    // Campi per la visualizzazione dell'email
    @FXML private Label fromLabel;
    @FXML private Label toLabel;
    @FXML private Label subjectLabel;
    @FXML private Label dateLabel;
    @FXML private TextArea bodyArea;

    // Campi per la composizione dell'email
    @FXML private TextField toField;
    @FXML private TextField subjectField;
    @FXML private TextArea composeBodyArea;

    private ClientModel model;
    private EmailViewController emailViewController;
    private Email lastAttemptedEmail;

    public void initialize() {
        senderColumn.setCellValueFactory(new PropertyValueFactory<>("sender"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("sentDate"));

        emailTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                handleViewEmail();
            }
        });
    }

    public void setModel(ClientModel model) {
        this.model = model;
        emailTableView.setItems(model.getInbox());
    }

    public void setEmailViewController(EmailViewController emailViewController) {
        this.emailViewController = emailViewController;
    }

    @FXML
    private void handleCompose() {
        viewSection.setVisible(false);
        composeSection.setVisible(true);
    }

    @FXML
    private void handleSend() {
        Email email = new Email();
        email.setSender(model.getEmailAddress());
        email.setRecipients(Arrays.asList(toField.getText().split(",")));
        email.setSubject(subjectField.getText());
        email.setContent(composeBodyArea.getText());
        email.setSentDate(LocalDateTime.now());

        lastAttemptedEmail = email;

        if (sendEmail(email)) {
            clearComposeFields();
            composeSection.setVisible(false);
        }
    }

    private boolean sendEmail(Email email) {
        try (Socket socket = new Socket("localhost", 5000);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject("SEND_EMAIL");
            out.writeObject(email);
            out.flush();

            Object response = in.readObject();
            if (response instanceof String && ((String) response).startsWith("ERROR")) {
                showAlert("Errore di invio", (String) response, Alert.AlertType.ERROR);
                return false;
            }

            showAlert("Email inviata", "L'email è stata inviata con successo.", Alert.AlertType.INFORMATION);
            return true;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            showAlert("Errore", "Si è verificato un errore durante l'invio dell'email.", Alert.AlertType.ERROR);
            return false;
        }
    }

    @FXML
    private void handleRetry() {
        if (lastAttemptedEmail != null) {
            if (sendEmail(lastAttemptedEmail)) {
                lastAttemptedEmail = null;
            }
        } else {
            showAlert("Nessuna email da inviare", "Non c'è nessuna email in sospeso da inviare.", Alert.AlertType.INFORMATION);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleCancel() {
        clearComposeFields();
        composeSection.setVisible(false);
    }

    private void clearComposeFields() {
        toField.clear();
        subjectField.clear();
        composeBodyArea.clear();
    }

    @FXML
    private void handleViewEmail() {
        Email selectedEmail = emailTableView.getSelectionModel().getSelectedItem();
        if (selectedEmail != null) {
            fromLabel.setText("From: " + selectedEmail.getSender());
            toLabel.setText("To: " + String.join(", ", selectedEmail.getRecipients()));
            subjectLabel.setText("Subject: " + selectedEmail.getSubject());
            dateLabel.setText("Date: " + selectedEmail.getSentDate().toString());
            bodyArea.setText(selectedEmail.getContent());

            composeSection.setVisible(false);
            viewSection.setVisible(true);
        }
    }

    public void startEmailListener() {
        new Thread(() -> {
            while (true) {
                try (Socket socket = new Socket("localhost", 5000);
                     ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                    System.out.println("Connected to server");
                    while (true) {
                        Object obj = in.readObject();
                        if (obj instanceof Email) {
                            Email newEmail = (Email) obj;
                            javafx.application.Platform.runLater(() -> handleNewEmail(newEmail));
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    System.out.println("Error connecting to server: " + e.getMessage());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        }).start();
    }

    public void handleNewEmail(Email newEmail) {
        model.addEmail(newEmail);
        javafx.application.Platform.runLater(() -> {
            emailTableView.refresh();
        });
    }
}
