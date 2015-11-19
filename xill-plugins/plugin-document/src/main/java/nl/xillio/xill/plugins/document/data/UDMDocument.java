package nl.xillio.xill.plugins.document.data;


import nl.xillio.udm.builders.DocumentBuilder;
import nl.xillio.xill.plugins.document.exceptions.ValidationException;

public interface UDMDocument {
    void applyTo(DocumentBuilder builder) throws ValidationException;

    boolean isNew();
}
