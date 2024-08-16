package com.example.progetto_shit.Client;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class EmailForwarder {
    private String serverAddress;
    private int serverPort;

    public EmailForwarder(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void forwardEmail(String selectedEmail, List<String> recipients) {
        try (Socket socket = new Socket(serverAddress, serverPort);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            // Creazione di un oggetto di richiesta per l'inoltro dell'email
            EmailForwardRequest request = new EmailForwardRequest(selectedEmail, recipients);
            out.writeObject(request);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// Classe di supporto per creare una richiesta di inoltro
class EmailForwardRequest implements java.io.Serializable {
    private String emailContent;
    private List<String> recipients;

    public EmailForwardRequest(String emailContent, List<String> recipients) {
        this.emailContent = emailContent;
        this.recipients = recipients;
    }

    public String getEmailContent() {
        return emailContent;
    }

    public List<String> getRecipients() {
        return recipients;
    }
}
