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

    opens com.example.progetto_shit to javafx.fxml;
    exports com.example.progetto_shit;
    exports com.example.progetto_shit.Server;
    opens com.example.progetto_shit.Server to javafx.fxml;
}