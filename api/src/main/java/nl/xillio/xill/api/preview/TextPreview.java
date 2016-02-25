package nl.xillio.xill.api.preview;

import nl.xillio.xill.api.data.MetadataExpression;

/**
 * This interface represents an object that can provide a plain text preview.
 *
 * @author Thomas Biesaart
 */
public interface TextPreview extends MetadataExpression {
    /**
     * Renders a plain text preview of this object.
     *
     * @return the preview
     */
    String getTextPreview();
}
