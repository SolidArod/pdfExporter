module com.simplified.tmddata {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires PDFViewerFX;
    requires org.apache.pdfbox;
    requires java.scripting;

    opens com.simplified.tmddata to javafx.fxml;
    exports com.simplified.tmddata;
}

