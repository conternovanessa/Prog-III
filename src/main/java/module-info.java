module com.example.progetto_shit {
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
    exports com.example.progetto_shit.Model;
    exports com.example.progetto_shit.Controller;
    exports com.example.progetto_shit.Main;

    // Apre i pacchetti per riflessione a javafx.fxml
    opens com.example.progetto_shit.Model to javafx.fxml;
    opens com.example.progetto_shit.Controller to javafx.fxml;
    opens com.example.progetto_shit.Main to javafx.fxml;
}
