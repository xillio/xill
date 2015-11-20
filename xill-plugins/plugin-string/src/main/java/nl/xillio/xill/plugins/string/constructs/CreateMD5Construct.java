package nl.xillio.xill.plugins.string.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.string.services.string.StringUtilityService;

import java.security.NoSuchAlgorithmException;

/**
 * <p>
 * Returns a MD5 hash of the given variable.
 * </p>
 *
 * @author Sander Visser
 */
public class CreateMD5Construct extends Construct {
    @Inject
    private StringUtilityService stringService;

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
        		value -> process(value, stringService),
        		new Argument("value", ATOMIC));
    }

    static MetaExpression process(final MetaExpression value, final StringUtilityService stringService) {
        assertNotNull(value, "value");
        try {
            return fromValue(stringService.createMD5Construct(value.getStringValue()));
        } catch (NoSuchAlgorithmException e) {
            throw new RobotRuntimeException("No such algorithm", e);
        }
    }
}
