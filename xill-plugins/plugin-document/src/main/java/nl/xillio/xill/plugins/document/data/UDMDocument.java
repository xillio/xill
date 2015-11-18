package nl.xillio.xill.plugins.document.data;


import nl.xillio.udm.builders.DocumentBuilder;

public interface UDMDocument {
    void applyTo(DocumentBuilder builder);
}
