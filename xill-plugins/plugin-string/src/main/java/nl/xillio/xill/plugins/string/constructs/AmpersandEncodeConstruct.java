package nl.xillio.xill.plugins.string.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.string.services.string.StringUtilityService;

/**
 * <p>
 * Encodes all special XML characters (&lt;,&gt;,&amp;,&quot;,&#39;) to their respective xml entities.
 * </p>
 *
 * @author Sander
 */
public class AmpersandEncodeConstruct extends Construct {
    @Inject
    private StringUtilityService stringUtils;

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
                string -> process(string, stringUtils),
                new Argument("String", ATOMIC));
    }

    static MetaExpression process(final MetaExpression stringVar, final StringUtilityService stringUtils) {
        assertNotNull(stringVar, "string");

        String text = stringVar.getStringValue();

        return fromValue(stringUtils.escapeXML(text));

    }

}
