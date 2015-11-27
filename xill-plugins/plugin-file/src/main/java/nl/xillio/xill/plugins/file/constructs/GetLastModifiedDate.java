package nl.xillio.xill.plugins.file.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.data.Date;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.api.data.DateFactory;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;

import java.io.IOException;
import java.nio.file.InvalidPathException;

/**
 * Construct for accessing creating time (ctime) on a file system.
 * @author Folkert van Verseveld
 */
public class GetLastModifiedDate extends Construct {
    private final DateFactory date;
    private final FileUtilities fileUtils;

    /**
     * Instantiate new creation time (ctime) construct using specified time conversion and file i/o utilities.
     * @param d The time conversion factory
     * @param f The file i/o factory
     */
    @Inject
    public GetLastModifiedDate(final DateFactory d, final FileUtilities f) {
        this.date = d;
        this.fileUtils = f;
    }

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext c) {
        return new ConstructProcessor(
            (file) -> process(c, date, fileUtils, file),
            new Argument("file", ATOMIC)
        );
    }

    static MetaExpression process(final ConstructContext c, final DateFactory date, final FileUtilities io, final MetaExpression expr) {
        try {
            Date d = date.from(io.getLastModifiedDate(getFile(c, expr.getStringValue())).toInstant());
            return fromValue(d.toString());
        } catch (InvalidPathException e) {
            throw new RobotRuntimeException("No such file: " + expr.getStringValue());
        } catch (IOException e) {
            throw new RobotRuntimeException("Failed to read attributes: " + e.getMessage(), e);
        }
    }
}
