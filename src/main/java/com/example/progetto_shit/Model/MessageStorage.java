package com.example.progetto_shit.Model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MessageStorage {

    private static final String BASE_DIR = "messages/";

    // Metodo per assicurarsi che la directory esista
    private static void ensureDirectoryExists() {
        File directory = new File(BASE_DIR);
        if (!directory.exists()) {
            directory.mkdirs(); // Crea la directory, inclusi eventuali genitori mancanti
        }
    }

    // Salva un messaggio per un destinatario specifico
    public static void saveMessage(String sender, String recipient, String subject, String body) {
        ensureDirectoryExists(); // Assicurati che la directory principale esista

        // Crea una directory per il destinatario se non esiste
        String clientDirPath = BASE_DIR + recipient;
        File clientDir = new File(clientDirPath);
        if (!clientDir.exists()) {
            clientDir.mkdirs();
        }

        // Sanitizza il nome del file per rimuovere caratteri non validi
        String sanitizedSubject = subject.replaceAll("[^a-zA-Z0-9.-]", "_");

        // Genera un nome file basato sul destinatario e l'oggetto della mail
        String fileName = recipient + "_" + sanitizedSubject + ".txt";
        String filePath = clientDirPath + "/" + fileName;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("From: " + sender);  // Scrive il mittente come prima riga
            writer.newLine();
            writer.write("Subject: " + subject);
            writer.newLine();
            writer.write("Body: " + body);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Restituisce la lista di messaggi per un destinatario specifico
    public static List<String> getMessagesForRecipient(String recipient) {
        List<String> messages = new ArrayList<>();
        String clientDirPath = BASE_DIR + recipient;
        File clientDir = new File(clientDirPath);

        if (!clientDir.exists() || !clientDir.isDirectory()) {
            return messages; // Nessun messaggio se la directory non esiste
        }

        // Leggi tutti i file nella directory del client
        File[] emailFiles = clientDir.listFiles((dir, name) -> name.endsWith(".txt"));
        if (emailFiles != null) {
            for (File emailFile : emailFiles) {
                try (BufferedReader reader = new BufferedReader(new FileReader(emailFile))) {
                    StringBuilder message = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        message.append(line).append("\n");
                    }
                    messages.add(message.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return messages;
    }
}