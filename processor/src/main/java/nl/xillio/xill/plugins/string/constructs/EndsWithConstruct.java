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
 * Returns whether the first string ends with the second string.
 * </p>
 *
 * @author Sander
 */
public class EndsWithConstruct extends Construct {

    @Inject
    StringUtilityService stringService;

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor((string, suffix) ->
                process(string, suffix, stringService), new Argument("string", ATOMIC), new Argument("suffix", ATOMIC));
    }

    static MetaExpression process(final MetaExpression string1, final MetaExpression string2, final StringUtilityService stringService) {
        assertNotNull(string1, "string1");
        assertNotNull(string2, "string2");

        return fromValue(stringService.endsWith(string1.getStringValue(), string2.getStringValue()));
    }
}
