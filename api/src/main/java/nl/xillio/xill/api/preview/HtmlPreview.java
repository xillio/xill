package nl.xillio.xill.api.preview;

import nl.xillio.xill.api.data.MetadataExpression;

/**
 * This interface represents an object that can provide an html preview.
 *
 * @author Thomas Biesaart
 */
public interface HtmlPreview extends MetadataExpression {
    /**
     * Render the HTML preview of this object.
     *
     * @return valid HTML5
     */
    String getHtmlPreview();
}
