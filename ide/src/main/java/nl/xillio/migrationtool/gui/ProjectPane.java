package nl.xillio.migrationtool.gui;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.swing.filechooser.FileFilter;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;
import nl.xillio.migrationtool.dialogs.DeleteFileDialog;
import nl.xillio.migrationtool.dialogs.DeleteProjectDialog;
import nl.xillio.migrationtool.dialogs.NewFolderDialog;
import nl.xillio.migrationtool.dialogs.NewProjectDialog;
import nl.xillio.migrationtool.dialogs.RenameDialog;
import nl.xillio.migrationtool.dialogs.UploadToServerDialog;
import nl.xillio.migrationtool.gui.WatchDir.FolderListener;
import nl.xillio.xill.api.Xill;
import nl.xillio.xill.util.settings.ProjectSettings;
import nl.xillio.xill.util.settings.Settings;
import nl.xillio.xill.util.settings.SettingsHandler;

public class ProjectPane extends AnchorPane implements FolderListener, ChangeListener<TreeItem<Pair<File, String>>> {
	private static final SettingsHandler settings = SettingsHandler.getSettingsHandler();
	private static final String DEFAULT_PROJECT_NAME = "Samples";
	private static final String DEFAULT_PROJECT_PATH = "./samples";
	private final BotFileFilter robotFileFilter = new BotFileFilter();

	@FXML
	private TreeView<Pair<File, String>> trvProjects;

	@FXML
	private Button btnAddFolder;
	@FXML
	private Button btnUpload;
	@FXML
	private Button btnRename;
	@FXML
	private Button btnDelete;
	/**
	 * A file filter filtering on the FXController.BOT_EXTENSION extension. Protected to avoid synthetic accessors.
	 */
	protected final BotFileFilter fileFilter = new BotFileFilter();
	private static WatchDir watcher;

	private final TreeItem<Pair<File, String>> root = new TreeItem<>(new Pair<>(new File("."), "Projects"));
	private FXController controller;

	/**
	 * Initialize UI stuff
	 */
	public ProjectPane() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ProjectPane.fxml"));
			loader.setClassLoader(getClass().getClassLoader());
			loader.setController(this);
			Node ui = loader.load();
			getChildren().add(ui);
		} catch (IOException e) {
			e.printStackTrace();
		}

		trvProjects.setRoot(root);
		trvProjects.getSelectionModel().selectedItemProperty().addListener(this);
		trvProjects.setShowRoot(false);
		root.setExpanded(true);

		try {
			watcher = new WatchDir();
			new Thread(watcher).start();
		} catch (IOException e) {}

		trvProjects.setCellFactory(treeView -> new TreeCell<Pair<File, String>>() {
			@Override
			protected void updateItem(final Pair<File, String> pair, final boolean empty) {
				super.updateItem(pair, empty);

				if (pair == null || pair.getValue() == null) {
					setText("");
					return;
				}

				setText(pair.getValue());

				setOnMouseClicked(event -> {
					// Double click
					if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() > 1) {
						if (pair.getKey() != null && pair.getKey().exists() && pair.getKey().isFile()) {
							// Open new tab from file
							controller.openFile(pair.getKey());
						}
					}
				});
			}
		});

		loadProjects();
	}

	@FXML
	private void newProjectButtonPressed() {
		new NewProjectDialog(this).show();
	}

	@FXML
	private void newFolderButtonPressed() {
		new NewFolderDialog(this, getCurrentItem()).show();
	}

	@FXML
	private void uploadButtonPressed() {
		new UploadToServerDialog(this, getCurrentItem()).show();
	}

	@FXML
	private void renameButtonPressed() {
		Tab orgTab = controller.findTab(getCurrentItem().getValue().getKey()); // org file
		String oldName = getCurrentItem().getValue().getValue();
		RenameDialog dlg = new RenameDialog(getCurrentItem());
		dlg.showAndWait();
		String newName = getCurrentItem().getValue().getValue();
		if (oldName != newName) {// name has changed
			if (orgTab != null) {// if tab with the org file is opened then close it and open new one
				Tab selectedTab = controller.getSelectedTab();
				if (orgTab == selectedTab) {
					selectedTab = null;
				}
				controller.closeTab(orgTab);
				controller.openFile(getCurrentItem().getValue().getKey());
				if (selectedTab != null) {
					controller.showTab((RobotTab) selectedTab);
				}
			}
		}
	}

	@FXML
	private void deleteButtonPressed() {
		TreeItem<Pair<File, String>> item = getCurrentItem();

		if (item == getCurrentProject()) {
			new DeleteProjectDialog(this, item).show();
		} else {
			new DeleteFileDialog(this, item).show();
		}
	}

	private void loadProjects() {
		Platform.runLater(() -> {
			List<ProjectSettings> projects = settings.project().getAll();
			if (projects.isEmpty()) {
				disableAllButtons();
				return;
			};
			projects.forEach(this::addProject);
			if (settings.simple().get(Settings.LICENSE, Settings.License) == null && new File(DEFAULT_PROJECT_PATH).exists()) {
				newProject(DEFAULT_PROJECT_NAME, DEFAULT_PROJECT_PATH, "");
			}

			root.getChildren().forEach(node -> node.setExpanded(false));
			root.setExpanded(true);
		});
	}

	/**
	 * Removes an item from the project list and keeps the files.
	 *
	 * @param item
	 *        the item to remove
	 */
	public void removeProject(final TreeItem<Pair<File, String>> item) {
		root.getChildren().remove(item);
		settings.project().delete(item.getValue().getValue());
	}

	/**
	 * Deletes a project and it's files.
	 *
	 * @param item
	 *        a project item
	 */
	public void deleteProject(final TreeItem<Pair<File, String>> item) {
		item.getValue().getKey().delete();
		removeProject(item);
	}

	/**
	 * Creates a new project.
	 *
	 * @param name
	 *        the name of the new project
	 * @param folder
	 *        the folder representing the project
	 * @param description
	 *        the description of the project
	 * @return whether creating the project was successful
	 */
	public boolean newProject(final String name, final String folder, final String description) {
		boolean projectDoesntExist = root.getChildren().parallelStream().map(TreeItem::getValue).map(Pair::getValue).noneMatch(n -> n.equalsIgnoreCase(name))
						&& findItemByPath(root, folder) == null;
		if (projectDoesntExist) {
			ProjectSettings project = new ProjectSettings(name, folder, description);
			settings.project().save(project);
			addProject(project);
		}
		return projectDoesntExist;
	}

	private void addProject(final ProjectSettings project) {
		// Check if the project still exists
		if (project.getFolder() == null) {
			return;
		}

		TreeItem<Pair<File, String>> projectNode = new ProjectTreeItem(new File(project.getFolder()), project.getName());
		root.getChildren().add(projectNode);

		if (watcher != null) {
			try {
				watcher.addFolderListener(this, Paths.get(project.getFolder()));
			} catch (IOException e) {}
		}
		projectNode.setExpanded(false);
		select(projectNode);
	}

	/**
	 * Selects an item in the treeview corresponding to given a path.
	 *
	 * @param path
	 *        the path of the item to select
	 */
	public void select(final String path) {
		select(findItemByPath(root, path));
	}

	private TreeItem<Pair<File, String>> findItemByPath(final TreeItem<Pair<File, String>> parent, final String path) {
		TreeItem<Pair<File, String>> resultItem = null;
		for (TreeItem<Pair<File, String>> item : parent.getChildren()) {
			if (path.equals(item.getValue().getKey().getPath())) {
				resultItem = item;
			} else {
				TreeItem<Pair<File, String>> child = findItemByPath(item, path);
				if (child != null) {
					resultItem = child;
				}
			}
		}
		return resultItem;
	}

	/**
	 * Refreshes the selection in the treeview.
	 *
	 * @param item
	 *        the clicked item
	 */
	public void select(final TreeItem<Pair<File, String>> item) {
		if (item != null) {
			trvProjects.getSelectionModel().clearSelection();
			trvProjects.getSelectionModel().select(item);
		}
	}

	/**
	 * Gets the selectionmodel from the treeview.
	 *
	 * @return the selectionmodel
	 */
	public MultipleSelectionModel<TreeItem<Pair<File, String>>> getSelectionModel() {
		return trvProjects.getSelectionModel();
	}

	@Override
	public void folderChanged(final Path dir, final Path child, final WatchEvent<Path> event) {
		for (TreeItem<Pair<File, String>> item : root.getChildren()) {
			if (item instanceof ProjectTreeItem) {
				ProjectTreeItem project = (ProjectTreeItem) item;
				if (dir.startsWith(project.getValue().getKey().getAbsolutePath())) {
					project.refresh();
				}
			}
		}
	}

	/**
	 * Returns the root of the tree.
	 *
	 * @return the root of the tree
	 */
	public TreeItem<Pair<File, String>> getRoot() {
		return root;
	}

	/**
	 * @return the currently selected node
	 */
	public TreeItem<Pair<File, String>> getCurrentItem() {
		return trvProjects.getSelectionModel().getSelectedItem();
	}

	/**
	 * Gets the first project node above the childItem.
	 *
	 * @param childItem
	 * @return the project node
	 */
	public TreeItem<Pair<File, String>> getProject(final TreeItem<Pair<File, String>> childItem) {
		TreeItem<Pair<File, String>> item = childItem;
		if (item == root) {
			return null;
		}
		if (item != null) {
			while (item.getParent() != root) {
				item = item.getParent();
			}
			return item;
		}
		return null;
	}

	/**
	 * Gets the first project node above the selected item.
	 *
	 * @return project node
	 */
	public TreeItem<Pair<File, String>> getCurrentProject() {
		return getProject(getCurrentItem());
	}

	/**
	 * Gets the project path of a node.
	 *
	 * @param file
	 * @return The project path if it exists
	 */
	public Optional<String> getProjectPath(final File file) {
		select(file.getAbsolutePath());
		Optional<String> projectPath = Optional.empty();
		TreeItem<Pair<File, String>> project = getCurrentProject();

		if (project != null) {
			projectPath = Optional.of(project.getValue().getKey().getPath());
		}

		return projectPath;
	}

	/**
	 * Stops the folder watcher.
	 */
	public static void stop() {
		if (watcher != null) {
			watcher.stop();
		}
	}

	// public Optional<File> getRobot(final TreeItem<Pair<File, String>> parent, final RobotID robotID) {
	// for (int i = 0; i < parent.getChildren().size(); i++) {
	// TreeItem<Pair<File, String>> c = parent.getChildren().get(i);
	// if (c.isLeaf()) {
	// RobotID rId = RobotID.getInstance(c.getValue().getKey());
	// if (rId == robotID) {
	// return Optional.of(c.getValue().getKey());
	// }
	// } else {
	// Optional<File> file = getRobot(c, robotID);
	// if (file.isPresent()) {
	// return file;
	// }
	// }
	// }
	// return Optional.empty();
	// }

	protected void setGlobalController(final FXController controller) {
		this.controller = controller;
	}

	/**
	 * A filefilter filtering on the FXController.BOT_EXTENSION extension.
	 */
	protected class BotFileFilter extends FileFilter implements FilenameFilter {
		@Override
		public boolean accept(final File file) {
			return file.isDirectory() && !file.getName().startsWith(".") || file.getName().endsWith("." + Xill.FILE_EXTENSION);
		}

		@Override
		public String getDescription() {
			return "Xillio bot script files (*" + Xill.FILE_EXTENSION + ")";
		}

		@Override
		public boolean accept(final File directory, final String fileName) {
			return accept(new File(directory, fileName));
		}
	}

	private class ProjectTreeItem extends TreeItem<Pair<File, String>> {

		private boolean isLeaf;
		private boolean isFirstTimeChildren = true;
		private boolean isFirstTimeLeaf = true;

		/**
		 * @param file
		 * @param name
		 */
		public ProjectTreeItem(final File file, final String name) {
			super(new Pair<>(file, name));
		}

		@Override
		public ObservableList<TreeItem<Pair<File, String>>> getChildren() {
			if (isFirstTimeChildren) {
				isFirstTimeChildren = false;
				super.getChildren().setAll(buildChildren(this));
			}
			return super.getChildren();
		}

		@Override
		public boolean isLeaf() {
			if (isFirstTimeLeaf) {
				isFirstTimeLeaf = false;
				isLeaf = getValue().getKey().isFile();
			}
			return isLeaf;
		}

		public void refresh() {
			Platform.runLater(() -> {
				if (trvProjects.getScene() != null && trvProjects.getSelectionModel().getSelectedItem() != null) {
					File selection = trvProjects.getSelectionModel().getSelectedItem().getValue().getKey();
					ProjectTreeItem.super.getChildren().setAll(buildChildren(ProjectTreeItem.this));
					select(selection.getAbsolutePath());
				}
			});
		}

		private ObservableList<TreeItem<Pair<File, String>>> buildChildren(final TreeItem<Pair<File, String>> TreeItem) {
			File f = TreeItem.getValue().getKey();
			ObservableList<TreeItem<Pair<File, String>>> children = FXCollections.observableArrayList();

			if (f != null && f.isDirectory()) {
				// Get a list with all files (folders and robots)
				File[] files = f.listFiles(robotFileFilter);

				// Sort the list of files
				Arrays.sort(files, (o1, o2) -> {
					// Put directories above files
					if (o1.isDirectory() && o2.isFile()) {
						return -1;
					} else if (o1.isFile() && o2.isDirectory()) {
						return 1;
						// Both are the same type, compare them normally
					} else {
						return o1.compareTo(o2);
					}
				});

				// Create tree items from all files, add them to the list
				for (File file : files) {
					ProjectTreeItem treeItem = new ProjectTreeItem(file, file.getName());
					children.add(treeItem);
				}
			}

			return children;
		}
	}

	/*
	 * This method is called when the selection in the tree view is changed
	 *
	 * @see javafx.beans.value.ChangeListener#changed(javafx.beans.value.ObservableValue, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void changed(final ObservableValue<? extends TreeItem<Pair<File, String>>> arg0, final TreeItem<Pair<File, String>> oldObject, final TreeItem<Pair<File, String>> newObject) {

		// Update the buttons
		// Enable all
		btnAddFolder.setDisable(false);
		btnDelete.setDisable(false);
		btnRename.setDisable(false);
		btnUpload.setDisable(false);

		if (newObject == null || newObject == trvProjects.getRoot()) {
			// Disable all
			disableAllButtons();

		} else if (newObject == getProject(newObject)) {
			// This is a project
			btnRename.setDisable(true);
		}
	}

	private void disableAllButtons() {
		btnAddFolder.setDisable(true);
		btnDelete.setDisable(true);
		btnRename.setDisable(true);
		btnUpload.setDisable(true);
	}
}
