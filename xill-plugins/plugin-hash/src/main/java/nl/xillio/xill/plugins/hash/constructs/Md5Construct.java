package nl.xillio.xill.plugins.hash.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.hash.services.HashService;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * Returns a MD5 hash of the provided text or file (content)
 *
 * @author Zbynek Hochmann
 */
public class Md5Construct extends Construct {
    @Inject
    private HashService hashService;

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
                (value, fromFile) -> process(value, fromFile, hashService),
        		new Argument("value", ATOMIC),
                new Argument("fromFile", FALSE, ATOMIC));
    }

    static MetaExpression process(final MetaExpression value, final MetaExpression fromFile, final HashService hashService) {
        assertNotNull(value, "value");
        assertNotNull(fromFile, "fromFile");
        try {
            return fromValue(hashService.md5(value.getStringValue(), fromFile.getBooleanValue()));
        } catch (NoSuchAlgorithmException e) {
            throw new RobotRuntimeException("Cannot do md5 hash: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RobotRuntimeException("Cannot do md5 hash: " + e.getMessage(), e);
        }
    }
}
