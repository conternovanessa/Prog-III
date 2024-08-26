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

            if (isReply) {
                // Appende la risposta al file esistente e aggiunge il messaggio originale
                appendTo(filePath, sender, body);
            } else {
                // Scrive un nuovo messaggio
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
                    writer.write("From: " + sender);
                    writer.newLine();
                    writer.write("Subject: " + subject);
                    writer.newLine();
                    writer.write(body);
                    writer.newLine();
                    writer.write("-----------------------------------");
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            writeLock.unlock(); // Rilascio del WriteLock
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

    public static List<String> getMessagesForRecipient(String recipient) {
        readLock.lock(); // Acquisizione del ReadLock
        try {
            List<String> messages = new ArrayList<>();
            String clientDirPath = BASE_DIR + recipient;
            File clientDir = new File(clientDirPath);

            if (clientDir.exists() && clientDir.isDirectory()) {
                File[] messageFiles = clientDir.listFiles((dir, name) -> name.endsWith(".txt"));

                if (messageFiles != null) {
                    for (File file : messageFiles) {
                        StringBuilder emailContent = new StringBuilder();
                        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                emailContent.append(line).append("\n");
                            }
                            messages.add(emailContent.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            return messages;
        } finally {
            readLock.unlock(); // Rilascio del ReadLock
        }
    }

    public static boolean deleteMessage(String recipient, String sender, String subject) {
        writeLock.lock(); // Acquisizione del WriteLock
        try {
            String clientDirPath = BASE_DIR + recipient;

            // Sanitizza i nomi
            String sanitizedSender = sender.replaceAll("[^a-zA-Z0-9@.-]", "_");
            String sanitizedSubject = subject.replaceAll("[^a-zA-Z0-9.-]", "_");

            // Costruisci il percorso del file
            String fileName = sanitizedSender + "_" + sanitizedSubject + ".txt";
            File emailFile = new File(clientDirPath, fileName);

            if (emailFile.exists()) {
                return emailFile.delete();
            } else {
                System.out.println("Email file not found: " + emailFile.getPath());
                return false;
            }
        } finally {
            writeLock.unlock(); // Rilascio del WriteLock
        }
    }
}
