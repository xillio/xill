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
 * Decodes all ampersand-encoded characters in the provided text.
 * </p>
 *
 * @author Sander
 */
public class AmpersandDecodeConstruct extends Construct {

    @Inject
    private StringUtilityService stringUtils;

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
                (string, passes) -> process(string, passes, stringUtils),
                new Argument("string", ATOMIC),
                new Argument("passes", fromValue(1), ATOMIC));
    }

    static MetaExpression process(final MetaExpression stringVar, final MetaExpression passesVar, final StringUtilityService stringUtils) {
        assertNotNull(stringVar, "string");
        assertNotNull(passesVar, "passes");

        String text = stringVar.getStringValue();

        int passes = passesVar.getNumberValue().intValue();

        return fromValue(stringUtils.unescapeXML(text, passes));
    }
}
