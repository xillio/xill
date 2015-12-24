package nl.xillio.xill.plugins.codec.hash.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.codec.hash.services.HashService;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * Returns a MD5 hash of the provided file
 */
public class FileToMD5Construct extends Construct {
    private final HashService hashService;

    @Inject
    public FileToMD5Construct(HashService hashService) {
        this.hashService = hashService;
    }

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
                this::process,
                new Argument("value", ATOMIC));
    }

    MetaExpression process(final MetaExpression value) {
        assertNotNull(value, "value");
        try {
            return fromValue(hashService.fileToMD5(value.getStringValue()));
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RobotRuntimeException("Cannot do md5 hash: " + e.getMessage(), e);
        }
    }
}
