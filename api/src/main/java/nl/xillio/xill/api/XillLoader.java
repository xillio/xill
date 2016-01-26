package nl.xillio.xill.api;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author Thomas Biesaart
 */
public class XillLoader {
    /**
     * Load a XillEnvironment from a specific folder.
     *
     * @param coreFolder the folder
     * @return the environment
     * @throws IOException if no environment could be found
     */
    public static XillEnvironment getEnv(Path coreFolder) throws IOException {
        JarFinder finder = new JarFinder();
        Files.walkFileTree(coreFolder, finder);

        for (Path jarFile : finder.getJarFiles()) {
            if (!jarFile.toString().contains("processor")) {
                // This check if to improve performance. We only load from processor jars
                URL url = jarFile.toUri().toURL();
                URLClassLoader classLoader = new URLClassLoader(new URL[]{url});
                ServiceLoader<XillEnvironment> loader = ServiceLoader.load(XillEnvironment.class, classLoader);

                for (XillEnvironment environment : loader) {
                    return environment;
                }
            }
        }

        throw new NoSuchFileException("No XillEnvirionment implementation found in " + coreFolder);
    }

    /**
     * This class will save a list of all .jar files.
     */
    private static class JarFinder extends SimpleFileVisitor<Path> {
        private static final PathMatcher JAR_MATCHER = FileSystems.getDefault().getPathMatcher("glob:**.jar");
        private final List<Path> jarFiles = new ArrayList<>();

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (JAR_MATCHER.matches(file)) {
                jarFiles.add(file);
            }
            return super.visitFile(file, attrs);
        }

        public List<Path> getJarFiles() {
            return jarFiles;
        }
    }
}
