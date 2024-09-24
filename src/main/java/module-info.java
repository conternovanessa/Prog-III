module com.progetto.nuovo_progetto {
    requires javafx.controls;
    requires javafx.fxml;

    exports com.progetto.nuovo_progetto;
    exports com.progetto.nuovo_progetto.client;
    exports com.progetto.nuovo_progetto.server;

    exports com.progetto.nuovo_progetto.client.controller;
    exports com.progetto.nuovo_progetto.server.controller;

    opens com.progetto.nuovo_progetto to javafx.fxml;
    opens com.progetto.nuovo_progetto.client to javafx.fxml;
    opens com.progetto.nuovo_progetto.server to javafx.fxml;

    opens com.progetto.nuovo_progetto.client.controller to javafx.fxml;
    opens com.progetto.nuovo_progetto.server.controller to javafx.fxml;
}