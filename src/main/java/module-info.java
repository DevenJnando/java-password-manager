module com.jamesd.passwordmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.jfoenix;
    requires commons.lang3;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires fontawesomefx;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires org.slf4j;
    requires com.azure.cosmos;
    requires java.sql;
    requires java.base;
    requires java.desktop;
    requires jbcrypt;

    opens com.jamesd.passwordmanager to javafx.fxml;
    opens com.jamesd.passwordmanager.Controllers to javafx.fxml;
    opens com.jamesd.passwordmanager.Wrappers to javafx.base;
    opens com.jamesd.passwordmanager.Models to com.fasterxml.jackson.databind;
    exports com.jamesd.passwordmanager;
    exports com.jamesd.passwordmanager.Controllers;
    exports com.jamesd.passwordmanager.Models;
    exports com.jamesd.passwordmanager.Wrappers;
}