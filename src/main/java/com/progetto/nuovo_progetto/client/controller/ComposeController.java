package com.progetto.nuovo_progetto.client.controller;

import com.progetto.nuovo_progetto.client.model.ClientModel;
import com.progetto.nuovo_progetto.common.Email;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class ComposeController {
    @FXML private TextField toField;
    @FXML private TextField subjectField;
    @FXML private TextArea bodyArea;
    @FXML private VBox composeSection;  // Referenza alla sezione di composizione

    private ClientModel model;

    // Lista di destinatari validi
    private final List<String> validRecipients = Arrays.asList(
            "filippoditto@progetto.com",
            "fabiodelia@progetto.com",
            "vanessaconterno@progetto.com"
    );

    public void setModel(ClientModel model) {
        this.model = model;
    }

    @FXML
    private void showComposeSection() {
        composeSection.setVisible(true);  // Rende visibile la sezione di composizione
    }

    /**
     * Invia l'email se i destinatari sono validi.
     */
    @FXML
    private void sendEmail() {
        String[] recipients = toField.getText().split(",");

        // Controlla se i destinatari sono validi
        if (!areRecipientsValid(recipients)) {
            showErrorDialog("Errore", "Destinatario non valido", "Uno o più destinatari non sono validi.");
            return;  // Esci dal metodo se uno o più destinatari non sono validi
        }

        // Crea l'email
        Email email = new Email();
        email.setSender(model.getEmailAddress());
        email.setRecipients(Arrays.asList(recipients));
        email.setSubject(subjectField.getText());
        email.setContent(bodyArea.getText());
        email.setSentDate(LocalDateTime.now());

        // Invia l'email
        try (Socket socket = new Socket("localhost", 5000);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            out.writeObject("SEND_EMAIL");
            out.writeObject(email);
            out.flush();

            // Pulisce i campi e chiude la sezione di composizione
            clearFields();
            composeSection.setVisible(false);

        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog("Errore di invio", "Invio fallito", "Si è verificato un errore durante l'invio dell'email.");
        }
    }

    /**
     * Verifica se i destinatari sono validi.
     * @param recipients L'array dei destinatari
     * @return true se tutti i destinatari sono validi, false altrimenti
     */
    private boolean areRecipientsValid(String[] recipients) {
        for (String recipient : recipients) {
            String trimmedRecipient = recipient.trim();
            if (!validRecipients.contains(trimmedRecipient)) {
                return false;  // Un destinatario non è valido
            }
        }
        return true;  // Tutti i destinatari sono validi
    }


    /**
     * Mostra un dialogo di errore.
     */
    private void showErrorDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleCancel() {
        clearFields();  // Pulisce i campi
        composeSection.setVisible(false);  // Nasconde la sezione di composizione
    }

    private void clearFields() {
        toField.clear();
        subjectField.clear();
        bodyArea.clear();
    }
}
