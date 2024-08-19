package com.example.progetto_shit.Server;

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
    public static void saveMessage(String recipient, String subject, String body) {
        ensureDirectoryExists(); // Assicurati che la directory esista prima di salvare il messaggio

        String filePath = BASE_DIR + recipient + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write("Subject: " + subject);
            writer.newLine();
            writer.write("Body: " + body);
            writer.newLine();
            writer.write("-----");
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Restituisce la lista di messaggi per un destinatario specifico
    public static List<String> getMessagesForRecipient(String recipient) {
        List<String> messages = new ArrayList<>();
        String filePath = BASE_DIR + recipient + ".txt";
        File file = new File(filePath);

        if (!file.exists()) {
            return messages;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder message = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals("-----")) {
                    messages.add(message.toString());
                    message.setLength(0); // Clear the StringBuilder
                } else {
                    message.append(line).append("\n");
                }
            }
            // Aggiungi l'ultimo messaggio se esiste
            if (message.length() > 0) {
                messages.add(message.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return messages;
    }
}
