package nl.xillio.xill.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Utility class that offers various generic file/io functions
 */
public class IOUtil {

	private static final Logger LOGGER = LogManager.getLogger(IOUtil.class);

	private static Map<String, List<URL>> urlCache = new Hashtable<>();

	/**
	 * Returns a list of files inside the application's jar file, or if there is none, from the filesystem
	 *
	 * @param clazz
	 *
	 * @param folder
	 *        The folder to look for
	 * @return A list of filenames; directories are skipped
	 */
	public static List<URL> readFolder(final Class<?> clazz, final String folder) {
		// The urlCache dramatically increases performance of jar-files, as they only have to be scanned once for each folder, instead of with every request
		if (urlCache.containsKey(folder)) {
			return urlCache.get(folder);
		}

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		// 1. Try to read from jarfile
		List<URL> urls = readJarFolder(clazz, folder);

		// 2. If that failed, then try to read the stuff straight from file
		if (urls == null || urls.isEmpty()) {
			try {
				urls = Arrays.stream(new File(classLoader.getResource(folder).toURI()).listFiles()).parallel()
					.filter(file -> !file.isDirectory())
					.map(file -> "file://" + file.getAbsolutePath() + "/" + file.getName())
					.map(stringUrl -> {
						Optional<URL> result = Optional.empty();
						try {
							result = Optional.of(new URL(stringUrl));
						} catch (Exception e) {
							LOGGER.error(e.getMessage(), e);
						}
						return result;
					})
					.filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		urlCache.put(folder, urls);
		return urls;
	}

	/**
	 * Returns a list of files inside the application's jar file
	 *
	 * @param folder
	 *        The folder to be looked up
	 * @return A list of filenames; directories are skipped
	 */
	private static List<URL> readJarFolder(final Class<?> clazz, final String folder) {
		CodeSource src = clazz.getProtectionDomain().getCodeSource();
		List<URL> list = new ArrayList<>();

		if (src != null) {
			URL jar = src.getLocation();
			try {
				ZipInputStream zip = new ZipInputStream(jar.openStream());
				ZipEntry zipEntry = null;
				while ((zipEntry = zip.getNextEntry()) != null) {
					if (!zipEntry.isDirectory() && zipEntry.getName().startsWith(folder)) {
						String url = src.getLocation() + "!/" + folder + "/" + zipEntry.getName();
						list.add(new URL(url));
					}
				}
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
				return null;
			}
		} else {
			return null;
		}
		return list;
	}

	/**
	 * Lists the constructs in the specified folder
	 *
	 * @param clazz
	 * @param constructPackage
	 * @return
	 */
	public static List<String> listConstructs(final Class<?> clazz, final String constructPackage) {
		List<String> constructNames = new LinkedList<>();
		try {
			constructNames = readFolder(clazz, constructPackage.replace('.', '/')).stream()
				.map(URL::getPath)
				.filter(path -> path.endsWith("Construct.class"))
				.collect(Collectors.toList());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return constructNames;
	}

}
