package nl.xillio.xill.util.db;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.xillio.xill.util.IOUtil;

public class DataBaseHandler {
	private static String CONNECTORPACKAGE = "nl.xillio.xill.util.db.connectors";
	private static String CONNECTORFOLDER = CONNECTORPACKAGE.replace(".", "/");

	private static Map<String, Connector> connectors = new HashMap<>();
	private static Map<String, Connection> connections = new HashMap<>();

	/**
	 * Initialize the databasehandler so it can load the connectors.
	 */
	static {
		loadConnectors();
	}

	private DataBaseHandler() {}

	private static void loadConnectors() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		List<URL> templates = IOUtil.readFolder(DataBaseHandler.class, CONNECTORFOLDER);

		for (URL url : templates) {
			String filename = url.getPath();
			if (filename.endsWith("Connector.class")) {
				filename = filename.substring(filename.lastIndexOf('/') + 1, filename.length() - 6);
				String classname = CONNECTORPACKAGE + "." + filename;

				try {
					Connector t = (Connector) classLoader.loadClass(classname).newInstance();
					connectors.put(t.getName(), t);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static Connector getConnector(final String name) {
		return connectors.get(name);
	}

	public static void registerConnection(final String name, final Connection connection) {
		if (name != null && connection != null) {
			connections.put(name, connection);
		}
	}

	public static Connection getConnection(final String name) {
		return connections.get(name);
	}

}
