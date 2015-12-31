package nl.xillio.xill.services.files;

import nl.xillio.xill.api.construct.ConstructContext;

import java.io.File;

/**
 * <p>
 * This class is the main implementation of the FileResolver
 * </p>
 *
 * @author Thomas Biesaart
 * @since 5-8-2015
 */
public class FileResolverImpl implements FileResolver {

    @Override
    public File buildFile(ConstructContext context, String path) {
        //First check if the provided path is absolute
        File file = new File(path);
        if (!file.isAbsolute()) {
            //It's not absolute so we make it relative to the robot
            file = new File(context.getRootRobot().getPath().getParentFile(), file.getPath());
        }
        return file;
    }
}
