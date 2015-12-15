package nl.xillio.migrationtool.gui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.*;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class WatchDir implements Runnable {

	private final WatchService watcher;
	private volatile Map<WatchKey, Path> keys;
	private volatile Map<FolderListener, List<Path>> listeners;

	private boolean stop = false;

	private static final Logger LOGGER = LogManager.getLogger(WatchDir.class);

	/**
	 * Creates a WatchService and registers the given directory
	 */
	WatchDir() throws IOException {
		watcher = FileSystems.getDefault().newWatchService();
		keys = new HashMap<WatchKey, Path>();
		listeners = new HashMap<FolderListener, List<Path>>();
	}

	public void addFolderListener(final FolderListener listener, final Path dir) throws IOException {
		List<Path> paths;
		if (listeners.containsKey(listener)) {
			paths = listeners.get(listener);
		} else {
			paths = new LinkedList<Path>();
			listeners.put(listener, paths);
		}

		if (!paths.contains(dir)) {
			paths.add(dir);
		}

		registerAll(dir);
	}

	public void stop() {
		stop = true;
		try {
			watcher.close();
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	private void fireEvent(final Path dir, final Path child, final WatchEvent<Path> event) {
		for (FolderListener listener : listeners.keySet()) {
			for (Path p : listeners.get(listener)) {
				if (dir.startsWith(p)) {
					listener.folderChanged(dir, child, event);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	static <T> WatchEvent<T> cast(final WatchEvent<?> event) {
		return (WatchEvent<T>) event;
	}

	/**
	 * Register the given directory with the WatchService
	 */
	private void registerDir(final Path dir) throws IOException {
		WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		keys.put(key, dir);
	}

	/**
	 * Register the given directory, and all its sub-directories, with the
	 * WatchService.
	 */
	private void registerAll(final Path start) throws IOException {
		// register directory and sub-directories
		Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs)
							throws IOException {
				registerDir(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	@Override
	@SuppressWarnings("squid:S1166") // InterruptedException thrown by watcher.take() is handled correctly
	public void run() {
		while (!stop) {

			// wait for key to be signalled
			WatchKey key;
			try {
				key = watcher.take();
			} catch (InterruptedException e) {
				return;
			}

			Path dir = keys.get(key);
			if (dir == null) {
				continue;
			}

			for (WatchEvent<?> event : key.pollEvents()) {
				Kind<?> kind = event.kind();

				// TBD - provide example of how OVERFLOW event is handled
				if (kind == OVERFLOW) {
					continue;
				}

				// Context for directory entry event is the file name of entry
				WatchEvent<Path> ev = cast(event);
				Path name = ev.context();
				Path child = dir.resolve(name);

				// print out event
				fireEvent(dir, child, ev);

				// if directory is created, and watching recursively, then
				// register it and its sub-directories
				if (kind == ENTRY_CREATE) { // recursive &&
					try {
						if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
							registerAll(child);
						}
					} catch (IOException e) {
						LOGGER.error(e.getMessage(), e);
					}
				}
			}

			// reset key and remove from set if directory no longer accessible
			boolean valid = key.reset();
			if (!valid) {
				keys.remove(key);

				// all directories are inaccessible
				if (keys.isEmpty()) {
					break;
				}
			}
		}
	}

	public interface FolderListener {
		public void folderChanged(final Path dir, final Path child, final WatchEvent<Path> event);
	}

}
