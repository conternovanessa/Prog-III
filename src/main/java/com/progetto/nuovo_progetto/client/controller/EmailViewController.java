package com.progetto.nuovo_progetto.client.controller;

import com.progetto.nuovo_progetto.common.Email;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class EmailViewController {
    @FXML private Label fromLabel;
    @FXML private Label toLabel;
    @FXML private Label subjectLabel;
    @FXML private Label dateLabel;
    @FXML private TextArea bodyArea;

    public void setEmail(Email email) {
        fromLabel.setText("From: " + email.getSender());
        toLabel.setText("To: " + String.join(", ", email.getRecipients()));
        subjectLabel.setText("Subject: " + email.getSubject());
        dateLabel.setText("Date: " + email.getSentDate().toString());
        bodyArea.setText("Body: " + email.getContent());
    }
}
