package com.example.progetto.Main;

import com.example.progetto.Controller.EmailDetailController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class EmailDetailApplication extends Application {

    private String emailContent;
    private String client;

    public EmailDetailApplication() {
        // Default constructor
    }

    public EmailDetailApplication(String emailContent, String client) {
        this.emailContent = emailContent;
        this.client = client;
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/progetto/View/email_detail_view.fxml"));
            Parent root = loader.load();

            EmailDetailController controller = loader.getController();
            controller.setStage(primaryStage);

            if (emailContent != null && client != null) {
                controller.setEmailContent(emailContent);
                controller.setClient(client);
            }

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Email Details");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load email detail view: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Metodi per impostare emailContent e client dopo la creazione dell'istanza
    public void setEmailContent(String emailContent) {
        this.emailContent = emailContent;
    }

    public void setClient(String client) {
        this.client = client;
    }
}