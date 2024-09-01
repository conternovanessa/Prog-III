package com.example.progetto.Controller;

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
        String clientDirPath = BASE_DIR + clientAddress;
        File clientDir = new File(clientDirPath);

        // Verifica che la directory esista
        if (!clientDir.exists() || !clientDir.isDirectory()) {
            System.out.println("Directory not found: " + clientDirPath);
            return "Nessun messaggio ricevuto.";
        }

        // Leggi tutti i file nella directory del client
        File[] emailFiles = clientDir.listFiles((dir, name) -> name.endsWith(".txt"));
        if (emailFiles == null || emailFiles.length == 0) {
            System.out.println("No email files found in directory: " + clientDirPath);
            return "Nessun messaggio ricevuto.";
        }

        for (File emailFile : emailFiles) {
            System.out.println("Reading email file: " + emailFile.getName());
            try (BufferedReader reader = new BufferedReader(new FileReader(emailFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    emails.append(line).append("\n");
                }
                emails.append("\n"); // Aggiungi una linea vuota per separare le email
            } catch (IOException e) {
                e.printStackTrace();
                return "Errore durante la lettura dei messaggi.";
            }
        }

        return emails.toString();
    }
}
