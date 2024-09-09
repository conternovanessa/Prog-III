package com.example.progetto.Model;

import com.example.progetto.Util.Logger;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MessageStorage {

    private static final String BASE_DIR = "messages/";
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();
    private static final Lock readLock = lock.readLock();
    private static final Lock writeLock = lock.writeLock();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private static final ConcurrentHashMap<String, ObservableList<String>> messages = new ConcurrentHashMap<>();

    public static void saveMessage(String sender, List<String> recipients, String subject, String body) {
        Logger.log("Saving message from " + sender + " to " + recipients);
        writeLock.lock();
        try {
            if (recipients.isEmpty()) {
                throw new IllegalArgumentException("La lista dei destinatari non può essere vuota.");
            }

            LocalDateTime now = LocalDateTime.now();
            String formattedDateTime = now.format(DATE_TIME_FORMATTER);

            for (String recipient : recipients) {
                String clientDirPath = BASE_DIR + recipient;
                ensureDirectoryExists(clientDirPath);

                String fileName = createFileName(formattedDateTime, sender, subject);
                Path filePath = Paths.get(clientDirPath, fileName);

                try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                    writer.write(String.format("Date: %s%n", formattedDateTime));
                    writer.write(String.format("From: %s%n", sender));
                    writer.write(String.format("To: %s%n", String.join(", ", recipients)));
                    writer.write(String.format("Subject: %s%n", subject));
                    writer.write(String.format("Body: %s%n", body));
                    writer.write("-----------------------------------\n");
                    Logger.log("Message saved to file: " + filePath);
                } catch (IOException e) {
                    Logger.log("Error writing to file: " + filePath + " " + e.getMessage());
                }
            }
        } finally {
            writeLock.unlock();
        }
    }


    public static synchronized void addMessage(String recipient, String message) {
        Logger.log("Adding message for recipient: " + recipient);
        ObservableList<String> recipientMessages = messages.computeIfAbsent(recipient, k -> FXCollections.observableArrayList());
        recipientMessages.add(message);
        Logger.log("Message added. Total messages for " + recipient + ": " + recipientMessages.size());
    }

    public static ObservableList<String> getMessagesForRecipient(String recipient) {
        Logger.log("Retrieving messages for recipient: " + recipient);
        ObservableList<String> recipientMessages = messages.computeIfAbsent(recipient, k -> FXCollections.observableArrayList());
        Logger.log("Retrieved " + recipientMessages.size() + " messages for " + recipient);
        return recipientMessages;
    }

    public static boolean markAsRead(String recipient, String sender, String subject) {
        Logger.log("Marking message as read for recipient: " + recipient + ", from: " + sender + ", subject: " + subject);
        writeLock.lock();
        try {
            Path clientDir = Paths.get(BASE_DIR, recipient);
            if (!Files.exists(clientDir) || !Files.isDirectory(clientDir)) {
                Logger.log("Directory not found or is not a directory: " + clientDir);
                return false;
            }

            String sanitizedSender = sanitizeFileName(sender);
            String sanitizedSubject = sanitizeFileName(subject);

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(clientDir,
                    path -> path.getFileName().toString().contains(sanitizedSender) &&
                            path.getFileName().toString().contains(sanitizedSubject))) {
                for (Path file : stream) {
                    Logger.log("Found matching file: " + file.getFileName());
                    List<String> lines = Files.readAllLines(file);
                    if (!lines.contains("READ")) {
                        lines.add("READ");
                        Files.write(file, lines);
                        Logger.log("Added READ marker to file: " + file.getFileName());
                        return true;
                    } else {
                        Logger.log("File already contains READ marker: " + file.getFileName());
                        return true;
                    }
                }
                Logger.log("No matching file found for marking as read");
            } catch (IOException e) {
                Logger.log("Error marking email as read: " + e.getMessage());
            }
            return false;
        } finally {
            writeLock.unlock();
        }
    }

    public static boolean isRead(String emailContent) {
        boolean read = emailContent.contains("READ");
        Logger.log("Checking if email is read: " + read);
        Logger.log("Email content (first 100 chars): " + emailContent.substring(0, Math.min(emailContent.length(), 100)));
        return read;
    }

    public static boolean deleteMessage(String recipient, String sender, String subject) {
        Logger.log("Attempting to delete message for recipient: " + recipient + ", from: " + sender + ", subject: " + subject);
        writeLock.lock();
        try {
            Path clientDir = Paths.get(BASE_DIR, recipient);
            if (!Files.exists(clientDir) || !Files.isDirectory(clientDir)) {
                Logger.log("Directory not found or is not a directory: " + clientDir);
                return false;
            }

            String sanitizedSender = sanitizeFileName(sender);
            String sanitizedSubject = sanitizeFileName(subject);

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(clientDir,
                    path -> path.getFileName().toString().contains(sanitizedSender) &&
                            path.getFileName().toString().contains(sanitizedSubject))) {
                for (Path file : stream) {
                    Files.delete(file);
                    Logger.log("Deleted file: " + file.getFileName());
                    return true;
                }
            } catch (IOException e) {
                Logger.log("Error deleting file: " + e.getMessage());
            }

            Logger.log("No matching file found for deletion");
            return false;
        } finally {
            writeLock.unlock();
        }
    }

    private static String readEmailContentFromFile(Path file) {
        try {
            String content = new String(Files.readAllBytes(file));
            Logger.log("Read email content from file: " + file.getFileName());
            return content;
        } catch (IOException e) {
            Logger.log("Error reading file: " + file + " " + e.getMessage());
            return null;
        }
    }

    private static void ensureDirectoryExists(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                Logger.log("Created directory: " + directoryPath);
            } else {
                Logger.log("Failed to create directory: " + directoryPath);
            }
        }
    }

    private static String createFileName(String formattedDateTime, String sender, String subject) {
        String sanitizedDateTime = formattedDateTime.replace(':', '-');
        String sanitizedSender = sanitizeFileName(sender);
        String sanitizedSubject = sanitizeFileName(subject);
        return sanitizedDateTime + "_" + sanitizedSender + "_" + sanitizedSubject + ".txt";
    }

    private static String sanitizeFileName(String name) {
        return name.trim().replaceAll("[^a-zA-Z0-9@._-]", "_");
    }
}
