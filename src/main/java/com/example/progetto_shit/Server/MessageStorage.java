package com.example.progetto_shit.Server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageStorage {

    private static Map<String, List<String>> messageMap = new HashMap<>();

    public static void saveMessage(String recipient, String subject, String body) {
        String message = "Oggetto: " + subject + "\nCorpo: " + body;

        messageMap.computeIfAbsent(recipient, k -> new ArrayList<>()).add(message);
    }

    public static List<String> getMessagesForRecipient(String recipient) {
        return messageMap.getOrDefault(recipient, new ArrayList<>());
    }
}
