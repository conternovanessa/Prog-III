package com.progetto.nuovo_progetto.client.controller;

import com.progetto.nuovo_progetto.client.model.ClientModel;
import com.progetto.nuovo_progetto.common.Email;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Arrays;

public class ComposeController {
    @FXML private TextField toField;
    @FXML private TextField subjectField;
    @FXML private TextArea bodyArea;

    private ClientModel model;

    public void setModel(ClientModel model) {
        this.model = model;
    }

    @FXML
    private void handleSend() {
        Email email = new Email();
        email.setSender(model.getEmailAddress());
        email.setRecipients(Arrays.asList(toField.getText().split(",")));
        email.setSubject(subjectField.getText());
        email.setBody(bodyArea.getText());
        email.setSentDate(LocalDateTime.now());

        sendEmail(email);
        closeWindow();
    }

    private void sendEmail(Email email) {
        try (Socket socket = new Socket("localhost", 5000);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            out.writeObject("SEND_EMAIL");
            out.writeObject(email);
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
            // Show an error dialog
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        ((Stage) toField.getScene().getWindow()).close();
    }
}