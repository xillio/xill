package nl.xillio.xill.plugins.system.services.info;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Singleton;

/**
 * This class contains some information about a file system
 */
@Singleton
public class FileSystemInfo {
	private final File[] roots;
	private final List<Map<String, Object>> properties = new ArrayList<>();

	/**
	 * Create a new {@link FileSystemInfo} for the main filesystem
	 */
	public FileSystemInfo() {
		roots = File.listRoots();

		for (File systemRoot : File.listRoots()) {
			Map<String, Object> rootInfo = new LinkedHashMap<>();
			rootInfo.put("path", systemRoot.getAbsolutePath());

			Map<String, Object> memory = new LinkedHashMap<>();
			memory.put("total", systemRoot.getTotalSpace());
			memory.put("free", systemRoot.getFreeSpace());
			memory.put("used", systemRoot.getTotalSpace() - systemRoot.getFreeSpace());
			rootInfo.put("storage", memory);

			properties.add(rootInfo);
		}
	}

	/**
	 * @return the roots
	 */
	public File[] getRoots() {
		return roots;
	}

	/**
	 * @return a list of properties for every root
	 */
	public List<Map<String, Object>> getRootProperties() {
		return properties;
	}

}
