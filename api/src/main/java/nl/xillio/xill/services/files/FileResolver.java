package nl.xillio.xill.services.files;

import com.google.inject.ImplementedBy;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ConstructContext;

import java.io.File;
import java.nio.file.Path;

/**
 * <p>
 * This interface describes a service that will resolve files from RobotIDs and paths.
 * </p>
 *
 * @author Thomas Biesaart
 * @since 5-8-2015
 */
@ImplementedBy(FileResolverImpl.class)
public interface FileResolver {
    /**
     * Resolves a file using the general file system rules.
     *
     * @param context the robot to resolve the file for
     * @param path    the path expression
     * @return the file
     */
    Path buildPath(ConstructContext context, MetaExpression path);

    @Deprecated
    File buildFile(ConstructContext context, String path);
}
