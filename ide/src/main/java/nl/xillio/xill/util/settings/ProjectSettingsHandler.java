package nl.xillio.xill.util.settings;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Class that contains common methods for storing the project settings
 * 
 * @author Zbynek Hochmann
 */
public class ProjectSettingsHandler {

	private static final String CATEGORY = "project";
	private static final String NAME = "name";
	private static final String KEYNAME = NAME;
	private static final String FOLDER = "folder";
	private static final String DESCRIPTION = "description";

	private ContentHandler content;

	private static final Logger LOGGER = LogManager.getLogger(ProjectSettingsHandler.class);

	ProjectSettingsHandler(ContentHandler content) {// Can be instantiated within package only
		this.content = content;
	}

	/**
	 * It stores the given project to settings
	 * 
	 * @param project The project data
	 */
	public void save(ProjectSettings project) {
		try {
			HashMap<String, Object> itemContent = new HashMap<>();
			itemContent.put(NAME, project.getName());
			itemContent.put(FOLDER, project.getFolder());
			itemContent.put(DESCRIPTION, project.getDescription());

			this.content.set(CATEGORY, itemContent, KEYNAME, project.getName());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * @return List of all projects in settings
	 */
	public List<ProjectSettings> getAll() {
		LinkedList<ProjectSettings> list = new LinkedList<>();

		try {
			List<Map<String, Object>> result = this.content.getAll(CATEGORY);
			for (Map<String, Object> item : result) {
				list.add(new ProjectSettings(item.get(NAME).toString(), item.get(FOLDER).toString(), item.get(DESCRIPTION).toString()));
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return list;
	}

	/**
	 * Deletes the project from settings (according to its the project name)
	 * 
	 * @param name The name of the project
	 */
	public void delete(final String name) {
		try {
			this.content.delete(CATEGORY, KEYNAME, name);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
}
