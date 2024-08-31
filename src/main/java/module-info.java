module com.example.progetto {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.logging;

    // Esporta i pacchetti principali del progetto
    exports com.example.progetto.Model;
    exports com.example.progetto.Controller;
    exports com.example.progetto.Main;

    // Apre i pacchetti per riflessione a javafx.fxml
    opens com.example.progetto.Model to javafx.fxml;
    opens com.example.progetto.Controller to javafx.fxml;
    opens com.example.progetto.Main to javafx.fxml;
}
