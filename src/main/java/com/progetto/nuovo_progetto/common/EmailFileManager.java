package com.progetto.nuovo_progetto.common;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EmailFileManager {
    private static final String BASE_DIR = "emails";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static EmailFileManager instance;

    private EmailFileManager() {}

    public static EmailFileManager getInstance() {
        if (instance == null) {
            instance = new EmailFileManager();
        }
        return instance;
    }

    // Generazione di un UUID univoco
    private String generateUUID() {
        return UUID.randomUUID().toString();
    }

    // Metodo per salvare un'email
    public void saveEmail(String sender, List<String> recipients, String subject, String content, LocalDateTime sentDate, boolean isRead) throws IOException {
        String emailId = generateUUID(); // Genera un UUID univoco per l'email

        for (String recipient : recipients) {
            String userDir = BASE_DIR + File.separator + recipient;
            Files.createDirectories(Paths.get(userDir));

            String fileName = userDir + File.separator + emailId + ".txt";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                writer.write("From: " + sender);
                writer.newLine();
                writer.write("To: " + String.join(", ", recipients));
                writer.newLine();
                writer.write("Subject: " + subject);
                writer.newLine();
                writer.write("Date: " + sentDate.format(DATE_FORMAT));
                writer.newLine();
                writer.write("Read: " + isRead);
                writer.newLine();
                writer.newLine();
                writer.write(content);
            }
        }
    }

    // Metodo per ottenere i riepiloghi delle email di un utente
    public List<Map<String, Object>> getEmailSummaries(String userEmail) throws IOException {
        String userDir = BASE_DIR + File.separator + userEmail;
        List<Map<String, Object>> summaries = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(userDir))) {
            for (Path path : stream) {
                if (Files.isRegularFile(path) && path.toString().endsWith(".txt")) {
                    Map<String, Object> summary = readEmailSummary(path);
                    if (summary != null) {
                        summaries.add(summary);
                    }
                }
            }
        }

        return summaries;
    }

    // Metodo per leggere il riepilogo di una email da file
    private Map<String, Object> readEmailSummary(Path path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            Map<String, Object> emailSummary = new HashMap<>();
            emailSummary.put("id", path.getFileName().toString().replace(".txt", ""));
            emailSummary.put("from", reader.readLine().substring(6));  // Salta "From: "
            emailSummary.put("to", reader.readLine().substring(4));    // Salta "To: "
            emailSummary.put("subject", reader.readLine().substring(9));  // Salta "Subject: "
            String dateStr = reader.readLine().substring(6);  // Salta "Date: "
            emailSummary.put("date", LocalDateTime.parse(dateStr, DATE_FORMAT));
            emailSummary.put("read", Boolean.parseBoolean(reader.readLine().substring(6)));  // Salta "Read: "

            return emailSummary;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Metodo per ottenere l'email completa
    public Map<String, Object> getFullEmail(String userEmail, String emailId) throws IOException {
        String fileName = BASE_DIR + File.separator + userEmail + File.separator + emailId + ".txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            Map<String, Object> fullEmail = new HashMap<>();
            fullEmail.put("from", reader.readLine().substring(6));
            fullEmail.put("to", reader.readLine().substring(4));
            fullEmail.put("subject", reader.readLine().substring(9));
            String dateStr = reader.readLine().substring(6);
            fullEmail.put("date", LocalDateTime.parse(dateStr, DATE_FORMAT));
            fullEmail.put("read", Boolean.parseBoolean(reader.readLine().substring(6)));
            reader.readLine();  // Salta la riga vuota
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            fullEmail.put("content", content.toString());

            return fullEmail;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Metodo per aggiornare lo stato di lettura di un'email
    public void updateEmailStatus(String userEmail, String emailId, boolean isRead) throws IOException {
        String fileName = BASE_DIR + File.separator + userEmail + File.separator + emailId + ".txt";
        Path path = Paths.get(fileName);
        List<String> lines = Files.readAllLines(path);

        // Aggiorna lo stato di lettura
        lines.set(4, "Read: " + isRead);

        Files.write(path, lines);
    }
}
