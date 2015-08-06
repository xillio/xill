package nl.xillio.xill.services.files;

import com.google.inject.ImplementedBy;
import nl.xillio.xill.api.components.RobotID;

import java.io.File;

/**
 * <p>
 * This interface describes a service that will resolve files from RobotIDs and paths
 * </p>
 *
 * @author Thomas Biesaart
 * @since 5-8-2015
 */
@ImplementedBy(FileResolverImpl.class)
public interface FileResolver {
	/**
	 * Resolve a file using the general file system rules
	 *
	 * @param robotID the robot to resolve the file for
	 * @param path    the path
	 * @return the file
	 */
	File buildFile(RobotID robotID, String path);
}
