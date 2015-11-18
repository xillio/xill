package nl.xillio.xill.plugins.document.services.xill;

import com.google.inject.ImplementedBy;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.document.data.UDMDocument;

/**
 * Created by Thomas Biesaart on 18/11/2015.
 */
@ImplementedBy(UDMDocumentFactory.class)
public interface UDMDocumentBuilder {
    UDMDocument build(MetaExpression expression);
}
