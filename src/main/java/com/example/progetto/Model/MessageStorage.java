package com.example.progetto.Model;

import java.io.*;
import java.nio.file.*;
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
            boolean created = directory.mkdirs();
            if (!created) {
                System.out.println("Failed to create directory: " + directoryPath);
            }
        }
    }

    public static void saveMessage(String sender, List<String> recipients, String subject, String body, boolean isReply) {
        writeLock.lock();
        try {
            if (recipients.isEmpty()) {
                throw new IllegalArgumentException("La lista dei destinatari non può essere vuota.");
            }
            for (String recipient : recipients) {
                String clientDirPath = BASE_DIR + recipient;
                ensureDirectoryExists(clientDirPath);

                String sanitizedSender = sanitizeFileName(sender);
                String sanitizedSubject = sanitizeFileName(subject);

                String fileName = sanitizedSender + "_" + sanitizedSubject + ".txt";
                Path filePath = Paths.get(clientDirPath, fileName);

                try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                    if (isReply) {
                        writer.write("From: " + sender);
                        writer.newLine();
                        writer.write("To: " + recipient);
                        writer.newLine();
                        writer.write("Subject: " + subject);
                        writer.newLine();
                        writer.write("Body: " + body);
                        writer.newLine();

                        String originalEmailContent = getOriginalEmailContent(recipient, sanitizedSender, sanitizedSubject);
                        if (originalEmailContent != null) {
                            writer.write(originalEmailContent);
                            writer.newLine();
                        }
                    } else {
                        writer.write("From: " + sender);
                        writer.newLine();
                        writer.write("To: " + String.join(", ", recipients));
                        writer.newLine();
                        writer.write("Subject: " + subject);
                        writer.newLine();
                        writer.write("Body: " + body);
                        writer.newLine();
                    }
                    writer.write("-----------------------------------");
                    writer.newLine();
                } catch (IOException e) {
                    System.out.println("Error writing to file: " + filePath + " " + e.getMessage());
                }
            }
        } finally {
            writeLock.unlock();
        }
    }

    public static String getOriginalEmailContent(String recipient, String sender, String subject) {
        readLock.lock();
        try {
            String clientDirPath = BASE_DIR + recipient;
            File clientDir = new File(clientDirPath);

            if (clientDir.exists() && clientDir.isDirectory()) {
                File[] messageFiles = clientDir.listFiles((dir, name) -> name.endsWith(".txt"));

                if (messageFiles != null) {
                    String sanitizedSender = sanitizeFileName(sender);
                    String sanitizedSubject = sanitizeFileName(subject);
                    for (File file : messageFiles) {
                        String fileName = file.getName();
                        if (fileName.contains(sanitizedSubject) && fileName.startsWith(sanitizedSender)) {
                            StringBuilder emailContent = new StringBuilder();
                            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    emailContent.append(line).append("\n");
                                }
                                return emailContent.toString();
                            } catch (IOException e) {
                                System.out.println("Error reading file: " + file.getPath() + " " + e.getMessage());
                            }
                        }
                    }
                }
            }
            return null;
        } finally {
            readLock.unlock();
        }
    }

    public static List<String> getMessagesForRecipient(String recipient) {
        readLock.lock();
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
                            System.out.println("Error reading file: " + file.getPath() + " " + e.getMessage());
                        }
                    }
                }
            }

            return messages;
        } finally {
            readLock.unlock();
        }
    }

    public static void markAsRead(String recipient, String sender, String subject) {
        writeLock.lock(); // Acquire write lock to modify the file
        try {
            String clientDirPath = BASE_DIR + recipient;
            File clientDir = new File(clientDirPath);

            if (!clientDir.exists() || !clientDir.isDirectory()) {
                System.out.println("Directory not found or is not a directory: " + clientDirPath);
                return;
            }

            // Sanitize file names
            String sanitizedSender = sanitizeFileName(sender);
            String sanitizedSubject = sanitizeFileName(subject);

            // Build expected file name
            String expectedFileName = sanitizedSender + "_" + sanitizedSubject + ".txt";
            File[] messageFiles = clientDir.listFiles((dir, name) -> name.equals(expectedFileName));

            if (messageFiles != null && messageFiles.length > 0) {
                File file = messageFiles[0]; // Assuming there's only one file with this name

                // Read the existing content
                StringBuilder emailContent = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        emailContent.append(line).append("\n");
                    }
                }

                // Check if it's already marked as read
                if (!emailContent.toString().contains("READ")) {
                    // Append "READ" marker
                    emailContent.append("READ\n");
                    emailContent.append("-----------------------------------\n");

                    // Write the updated content back to the file
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                        writer.write(emailContent.toString());
                    }
                }
            } else {
                System.out.println("No matching file found to mark as read for sender: " + sender + " and subject: " + subject);
            }
        } catch (IOException e) {
            System.out.println("Exception occurred while marking email as read: " + e.getMessage());
            e.printStackTrace();
        } finally {
            writeLock.unlock(); // Release write lock
        }
    }


    public static boolean deleteMessage(String recipient, String sender, String subject) {
        writeLock.lock(); // Acquisizione del WriteLock
        try {
            // Costruisci il percorso della directory del destinatario
            String clientDirPath = BASE_DIR + recipient;
            File clientDir = new File(clientDirPath);

            // Verifica se la directory del destinatario esiste e se è una directory
            if (!clientDir.exists() || !clientDir.isDirectory()) {
                System.out.println("Directory not found or is not a directory: " + clientDirPath);
                return false;
            }

            // Sanitize file names
            String sanitizedSender = sanitizeFileName(sender);
            String sanitizedSubject = sanitizeFileName(subject);

            // Costruisci il nome del file da cercare
            String expectedFileName = sanitizedSender + "_" + sanitizedSubject + ".txt";

            // Elenco tutti i file nella directory
            File[] messageFiles = clientDir.listFiles((dir, name) -> name.endsWith(".txt"));

            if (messageFiles != null) {
                boolean fileDeleted = false;
                for (File file : messageFiles) {
                    String fileName = file.getName();

                    // Stampa i nomi dei file trovati per il debug
                    System.out.println("Found file: " + fileName);

                    // Controlla se il nome del file corrisponde al formato atteso
                    if (fileName.equals(expectedFileName)) {
                        System.out.println("Attempting to delete file: " + file.getAbsolutePath());

                        if (file.delete()) {
                            System.out.println("File deleted successfully.");
                            fileDeleted = true;
                            break; // Esci dal ciclo dopo aver trovato e eliminato il file
                        } else {
                            System.out.println("Failed to delete file: " + file.getAbsolutePath());
                            return false;
                        }
                    }
                }
                if (!fileDeleted) {
                    System.out.println("No matching file found to delete for sender: " + sender + " and subject: " + subject);
                }
                return fileDeleted;
            } else {
                System.out.println("No .txt files found in directory: " + clientDirPath);
            }

            return false;

        } catch (Exception e) {
            System.out.println("Exception occurred while deleting file: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            writeLock.unlock(); // Rilascio del WriteLock
        }
    }

    private static String sanitizeFileName(String name) {
        return name.trim().replaceAll("[^a-zA-Z0-9@._-]", "_");
    }
}
