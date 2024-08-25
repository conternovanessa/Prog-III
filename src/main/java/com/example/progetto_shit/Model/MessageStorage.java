package com.example.progetto_shit.Model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MessageStorage {

    private static final String BASE_DIR = "messages/";
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();
    private static final Lock readLock = lock.readLock();
    private static final Lock writeLock = lock.writeLock();

    private static void ensureDirectoryExists(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public static void saveMessage(String sender, String recipient, String subject, String body, boolean isReply) {
        writeLock.lock(); // Acquisizione del WriteLock
        try {
            String clientDirPath = BASE_DIR + recipient;
            ensureDirectoryExists(clientDirPath);

            // Sanitizzazione dei nomi per evitare caratteri non validi
            String sanitizedSender = sender.replaceAll("[^a-zA-Z0-9@.-]", "_");
            String sanitizedSubject = subject.replaceAll("[^a-zA-Z0-9.-]", "_");

            // Nome del file che conterrà il messaggio
            String fileName = sanitizedSender + "_" + sanitizedSubject + ".txt";
            String filePath = clientDirPath + "/" + fileName;

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
                if (isReply) {
                    // Aggiunge la risposta al file esistente
                    writer.newLine();
                    writer.write("Reply from: " + sender);
                    writer.newLine();
                    writer.write(body);
                    writer.newLine();
                } else {
                    // Scrive un nuovo messaggio
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
        } finally {
            writeLock.unlock(); // Rilascio del WriteLock
        }
    }



    public static List<String> getMessagesForRecipient(String recipient) {
        readLock.lock(); // Acquisizione del ReadLock
        try {
            List<String> messages = new ArrayList<>();
            String clientDirPath = BASE_DIR + recipient;
            File clientDir = new File(clientDirPath);

            if (!clientDir.exists() || !clientDir.isDirectory()) {
                System.out.println("Directory not found: " + clientDirPath);
                return messages;
            }

            File[] emailFiles = clientDir.listFiles((dir, name) -> name.endsWith(".txt"));
            if (emailFiles != null) {
                for (File emailFile : emailFiles) {
                    System.out.println("Reading file: " + emailFile.getPath());
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
            } else {
                System.out.println("No email files found in directory: " + clientDirPath);
            }

            return messages;
        } finally {
            readLock.unlock(); // Rilascio del ReadLock
        }
    }

    private static void appendTo(String filePath, String sender, String replyContent) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.newLine();
            writer.write("Reply from: " + sender);
            writer.newLine();
            writer.write(replyContent);
            writer.newLine();
            writer.write("-----------------------------------");
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
