package com.progetto.nuovo_progetto.client.controller;

import com.progetto.nuovo_progetto.client.model.ClientModel;
import com.progetto.nuovo_progetto.common.Email;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Arrays;
import javafx.scene.web.WebView;

public class ClientController {
    @FXML private TableView<Email> emailTableView;
    @FXML private TableColumn<Email, String> senderColumn;
    @FXML private TableColumn<Email, String> subjectColumn;
    @FXML private TableColumn<Email, LocalDateTime> dateColumn;

    @FXML private VBox rightSection;
    @FXML private VBox viewSection;
    @FXML private VBox composeSection;

    @FXML private Label fromLabel;
    @FXML private Label toLabel;
    @FXML private Label subjectLabel;
    @FXML private Label dateLabel;
    @FXML private TextArea bodyArea;

    @FXML private TextField toField;
    @FXML private TextField subjectField;
    @FXML private TextArea composeBodyArea;

    @FXML private WebView bodyWebView;
    @FXML private Label bodyLabel;

    private ClientModel model;
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

        emailTableView.setRowFactory(tv -> new TableRow<Email>() {
            @Override
            protected void updateItem(Email email, boolean empty) {
                super.updateItem(email, empty);
                if (email == null) {
                    setStyle("");
                } else if (!email.isRead()) {
                    setStyle("-fx-font-weight: bold;");
                } else {
                    setStyle("");
                }
            }
        });

        emailTableView.getSortOrder().add(dateColumn);
        emailTableView.sort();
    }

    public void setModel(ClientModel model) {
        this.model = model;
        emailTableView.setItems(model.getInbox());
        fetchEmails();
    }

    private void fetchEmails() {
        model.fetchEmails();
        Platform.runLater(() -> {
            emailTableView.refresh();
            emailTableView.sort();
        });
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

            String response = (String) in.readObject();
            if (response.startsWith("SUCCESS")) {
                showAlert("Email inviata", "L'email è stata inviata con successo.", Alert.AlertType.INFORMATION);
                return true;
            } else {
                showAlert("Errore", response, Alert.AlertType.ERROR);
                return false;
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            showAlert("Errore", "Si è verificato un errore durante l'invio dell'email: " + e.getMessage(), Alert.AlertType.ERROR);
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

            // Usa una WebView per visualizzare il contenuto HTML
            bodyWebView.getEngine().loadContent(selectedEmail.getContent());

            // Imposta anche una label per il corpo del messaggio (per email non HTML)
            bodyLabel.setText(selectedEmail.getContent());

            composeSection.setVisible(false);
            viewSection.setVisible(true);

            if (!selectedEmail.isRead()) {
                selectedEmail.setRead(true);
                model.updateEmailStatus(selectedEmail);
                emailTableView.refresh();
            }
        }
    }

    public void startEmailUpdateTimer() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            fetchEmails();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void handleNewEmail(Email newEmail) {
        Platform.runLater(() -> {
            model.addEmail(newEmail);
            emailTableView.refresh();
            emailTableView.sort();
        });
    }
}
