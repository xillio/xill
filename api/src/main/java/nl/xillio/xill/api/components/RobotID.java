package nl.xillio.xill.api.components;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;

/**
 *
 */
public class RobotID {
	private static Map<String, RobotID> ids = new Hashtable<>();
	private final File path;
	private final File projectPath;

	private RobotID(final File path, final File projectPath) {
		this.path = path;
		this.projectPath = projectPath;
	}

	/**
	 * Returns the path associated with this id.
	 *
	 * @return the path associated with this id
	 */
	public File getPath() {
		return path;
	}

	@Override
	public String toString() {
		return path.getAbsolutePath();
	}

	/**
	 * Gets/creates a robotID that is singular for every path. This function normalizes the path before processing.
	 *
	 * @param file
	 *        the robot file
	 * @param projectPath
	 * @return a unique robot id for this path
	 */
	public static RobotID getInstance(final File file, final File projectPath) {

		String identity = file.getAbsolutePath() + "in" + projectPath.getAbsolutePath();

		RobotID id = ids.get(identity);

		if (id == null) {
			id = new RobotID(file, projectPath);
			ids.put(identity, id);
		}

		return id;
	}

	/**
	 * @return the projectPath
	 */
	public File getProjectPath() {
		return projectPath;
	}

}
