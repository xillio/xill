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
 * Returns true when the first value contains the second value.
 * </p>
 *
 * @author Sander
 */
public class ContainsConstruct extends Construct {

    @Inject
    private StringUtilityService stringService;

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
                (haystack, needle) -> process(haystack, needle, stringService),
                new Argument("haystack", ATOMIC, LIST),
                new Argument("needle", ATOMIC));
    }

    static MetaExpression process(final MetaExpression haystack, final MetaExpression needle, final StringUtilityService stringService) {
        // If either is null then false.
        if (haystack == NULL || needle == NULL) {
            return fromValue(false);
        }

        // Compare strings
        String value1 = haystack.getStringValue();
        String value2 = needle.getStringValue();
        return fromValue(stringService.contains(value1, value2));
    }

}
