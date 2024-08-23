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

    public static void saveMessage(String sender, String recipient, String subject, String body, boolean isReply) {
        ensureDirectoryExists();

        String clientDirPath = BASE_DIR + recipient;
        File clientDir = new File(clientDirPath);
        if (!clientDir.exists()) {
            clientDir.mkdirs();
        }

        // Sostituisci caratteri non validi nell'indirizzo email del mittente e nell'oggetto
        String sanitizedSender = sender.replaceAll("[^a-zA-Z0-9@.-]", "_");
        String sanitizedSubject = subject.replaceAll("[^a-zA-Z0-9.-]", "_");

        // Usa l'indirizzo email del mittente e l'oggetto per creare il nome del file
        String fileName = sanitizedSender + "_" + sanitizedSubject + ".txt";
        String filePath = clientDirPath + "/" + fileName;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            if (isReply) {
                writer.newLine();
                writer.write("Reply from: " + sender);
                writer.newLine();
                writer.write(body);
                writer.newLine();
            } else {
                writer.write("From: " + sender);
                writer.newLine();
                writer.write("Subject: " + subject);
                writer.newLine();
                writer.write(body);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getMessagesForRecipient(String recipient) {
        List<String> messages = new ArrayList<>();
        String clientDirPath = BASE_DIR + recipient;
        File clientDir = new File(clientDirPath);

        if (!clientDir.exists() || !clientDir.isDirectory()) {
            return messages;
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
