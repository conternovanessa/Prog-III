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

    public void saveEmail(Email email) throws IOException {
        for (String recipient : email.getRecipients()) {
            String userDir = BASE_DIR + File.separator + recipient;
            Files.createDirectories(Paths.get(userDir));

            String fileName = userDir + File.separator + email.getId() + ".txt";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                writer.write("From: " + email.getSender());
                writer.newLine();
                writer.write("To: " + String.join(", ", email.getRecipients()));
                writer.newLine();
                writer.write("Subject: " + email.getSubject());
                writer.newLine();
                writer.write("Date: " + email.getSentDate().format(DATE_FORMAT));
                writer.newLine();
                writer.write("Read: " + email.isRead());
                writer.newLine();
                writer.newLine();
                writer.write(email.getContent());
            }
        }
    }

    public List<Email> getEmailSummaries(String userEmail) throws IOException {
        String userDir = BASE_DIR + File.separator + userEmail;
        List<Email> summaries = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(userDir))) {
            for (Path path : stream) {
                if (Files.isRegularFile(path) && path.toString().endsWith(".txt")) {
                    Email summary = readEmailSummary(path);
                    if (summary != null) {
                        summaries.add(summary);
                    }
                }
            }
        }

        return summaries;
    }

    private Email readEmailSummary(Path path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            String from = reader.readLine().substring(6);  // Skip "From: "
            String to = reader.readLine().substring(4);    // Skip "To: "
            String subject = reader.readLine().substring(9);  // Skip "Subject: "
            String dateStr = reader.readLine().substring(6);  // Skip "Date: "
            boolean read = Boolean.parseBoolean(reader.readLine().substring(6));  // Skip "Read: "

            long id = Long.parseLong(path.getFileName().toString().replace(".txt", ""));

            Email email = new Email(from, Arrays.asList(to.split(", ")), subject, "");
            email.setId(id);
            email.setSentDate(LocalDateTime.parse(dateStr, DATE_FORMAT));
            email.setRead(read);

            return email;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Email getFullEmail(String userEmail, long id) throws IOException {
        String fileName = BASE_DIR + File.separator + userEmail + File.separator + id + ".txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String from = reader.readLine().substring(6);
            String to = reader.readLine().substring(4);
            String subject = reader.readLine().substring(9);
            String dateStr = reader.readLine().substring(6);
            boolean read = Boolean.parseBoolean(reader.readLine().substring(6));
            reader.readLine();  // Skip empty line
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

            Email email = new Email(from, Arrays.asList(to.split(", ")), subject, content.toString());
            email.setId(id);
            email.setSentDate(LocalDateTime.parse(dateStr, DATE_FORMAT));
            email.setRead(read);

            return email;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateEmailStatus(String userEmail, long id, boolean read) throws IOException {
        String fileName = BASE_DIR + File.separator + userEmail + File.separator + id + ".txt";
        Path path = Paths.get(fileName);
        List<String> lines = Files.readAllLines(path);

        // Update the read status
        lines.set(4, "Read: " + read);

        Files.write(path, lines);
    }
}