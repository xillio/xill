package nl.xillio.xill.plugins.file.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.data.Date;
import nl.xillio.xill.api.data.DateFactory;
import nl.xillio.xill.api.errors.OperationFailedException;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

/**
 * This class represents the base for all property constructs that extract {@link FileTime}.
 *
 * @author Thomas Biesaart
 */
abstract class AbstractDateFilePropertyConstruct extends AbstractFilePropertyConstruct<FileTime> {

    private DateFactory dateFactory;

    @Inject
    void setDateFactory(DateFactory dateFactory) {
        this.dateFactory = dateFactory;
    }

    @Override
    protected MetaExpression parse(FileTime time) {
        Date date = dateFactory.from(time.toInstant());

        MetaExpression result = fromValue(date.toString());
        result.storeMeta(date);
        return result;
    }

    protected BasicFileAttributes attributes(Path path) {
        try {
            return Files.readAttributes(path, BasicFileAttributes.class);
        } catch (IOException e) {
            throw new OperationFailedException("read attributes from " + path, e.getMessage(), e);
        }
    }
}
