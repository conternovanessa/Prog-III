package com.example.progetto_shit.Model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MessageStorage {

    private static final String BASE_DIR = "messages/";

    private static void ensureDirectoryExists() {
        File directory = new File(BASE_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public static void saveMessage(String sender, String recipient, String subject, String body) {
        ensureDirectoryExists();

        String clientDirPath = BASE_DIR + recipient;
        File clientDir = new File(clientDirPath);
        if (!clientDir.exists()) {
            clientDir.mkdirs();
        }

        String sanitizedSubject = subject.replaceAll("[^a-zA-Z0-9.-]", "_");
        String fileName = sanitizedSubject + ".txt";
        String filePath = clientDirPath + "/" + fileName;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("From: " + sender);  // Scrive il mittente come prima riga
            writer.newLine();
            writer.write("Subject: " + subject);
            writer.newLine();
            writer.write(body);  // Scrive il corpo del messaggio
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getMessagesForRecipient(String recipient) {
        List<String> messages = new ArrayList<>();
        String clientDirPath = BASE_DIR + recipient;
        File clientDir = new File(clientDirPath);

        if (!clientDir.exists() || !clientDir.isDirectory()) {
            return messages; // Nessun messaggio se la directory non esiste
        }

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
