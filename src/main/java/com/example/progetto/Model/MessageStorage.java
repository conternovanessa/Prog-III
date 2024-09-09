package com.example.progetto.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public static void saveMessage(String sender, List<String> recipients, String subject, String body) {
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
                } catch (IOException e) {
                    System.err.println("Error writing to file: " + filePath + " " + e.getMessage());
                }
            }
        } finally {
            writeLock.unlock();
        }
    }


    public static ObservableList<String> getMessagesForRecipient(String recipient) {
        readLock.lock();
        try {
            ObservableList<String> messages = FXCollections.observableArrayList();
            Path clientDir = Paths.get(BASE_DIR, recipient);

            if (Files.exists(clientDir) && Files.isDirectory(clientDir)) {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(clientDir, "*.txt")) {
                    for (Path file : stream) {
                        String emailContent = readEmailContentFromFile(file);
                        if (emailContent != null) {
                            messages.add(emailContent);
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Error reading directory: " + clientDir + " " + e.getMessage());
                }
            }

            return messages;
        } finally {
            readLock.unlock();
        }
    }

    public static boolean markAsRead(String recipient, String sender, String subject) {
        writeLock.lock();
        try {
            Path clientDir = Paths.get(BASE_DIR, recipient);
            if (!Files.exists(clientDir) || !Files.isDirectory(clientDir)) {
                System.err.println("Directory not found or is not a directory: " + clientDir);
                return false;
            }

            String sanitizedSender = sanitizeFileName(sender);
            String sanitizedSubject = sanitizeFileName(subject);

            System.out.println("Searching for email file:");
            System.out.println("Directory: " + clientDir);
            System.out.println("Sender: " + sanitizedSender);
            System.out.println("Subject: " + sanitizedSubject);

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(clientDir,
                    path -> path.getFileName().toString().contains(sanitizedSender) &&
                            path.getFileName().toString().contains(sanitizedSubject))) {
                for (Path file : stream) {
                    System.out.println("Found matching file: " + file.getFileName());
                    List<String> lines = Files.readAllLines(file);
                    if (!lines.contains("READ")) {
                        lines.add("READ");
                        Files.write(file, lines);
                        System.out.println("Added READ marker to file");
                        return true;
                    } else {
                        System.out.println("File already contains READ marker");
                        return true;
                    }
                }
                System.out.println("No matching file found");
            } catch (IOException e) {
                System.err.println("Error marking email as read: " + e.getMessage());
            }
            return false;
        } finally {
            writeLock.unlock();
        }
    }

    public static boolean isRead(String emailContent) {
        boolean read = emailContent.contains("READ");
        System.out.println("Checking if email is read: " + read);
        System.out.println("Email content (first 100 chars): " + emailContent.substring(0, Math.min(emailContent.length(), 100)));
        return read;
    }



    public static boolean deleteMessage(String recipient, String sender, String subject) {
        writeLock.lock();
        try {
            Path clientDir = Paths.get(BASE_DIR, recipient);
            if (!Files.exists(clientDir) || !Files.isDirectory(clientDir)) {
                System.err.println("Directory not found or is not a directory: " + clientDir);
                return false;
            }

            String sanitizedSender = sanitizeFileName(sender);
            String sanitizedSubject = sanitizeFileName(subject);

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(clientDir,
                    path -> path.getFileName().toString().contains(sanitizedSender) &&
                            path.getFileName().toString().contains(sanitizedSubject))) {
                for (Path file : stream) {
                    Files.delete(file);
                    return true;
                }
            } catch (IOException e) {
                System.err.println("Error deleting file: " + e.getMessage());
            }

            return false;
        } finally {
            writeLock.unlock();
        }
    }

    private static String readEmailContentFromFile(Path file) {
        try {
            return new String(Files.readAllBytes(file));
        } catch (IOException e) {
            System.err.println("Error reading file: " + file + " " + e.getMessage());
            return null;
        }
    }

    private static void ensureDirectoryExists(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                System.err.println("Failed to create directory: " + directoryPath);
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