package com.example.progetto_shit.Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ReceivedMailsHandler {

    private String clientAddress;
    private static final String BASE_DIR = "messages/";

    public ReceivedMailsHandler(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public String getReceivedMails() {
        StringBuilder emails = new StringBuilder();
        String filePath = BASE_DIR + clientAddress + ".txt";
        File file = new File(filePath);

        if (!file.exists()) {
            return "Nessun messaggio ricevuto.";
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int count = 1;
            while ((line = reader.readLine()) != null) {
                emails.append(count).append(". ").append(line).append("\n");
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Errore durante la lettura dei messaggi.";
        }

        return emails.toString();
    }
}
