package nl.xillio.migrationtool.gui;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.swing.filechooser.FileFilter;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.*;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;
import me.biesaart.utils.FileUtils;
import nl.xillio.migrationtool.dialogs.*;
import nl.xillio.migrationtool.gui.WatchDir.FolderListener;
import nl.xillio.xill.api.Xill;
import nl.xillio.xill.util.HotkeysHandler;
import nl.xillio.xill.util.settings.ProjectSettings;
import nl.xillio.xill.util.settings.Settings;
import nl.xillio.xill.util.settings.SettingsHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class ProjectPane extends AnchorPane implements FolderListener, ChangeListener<TreeItem<Pair<File, String>>>, EventHandler<Event> {
    private static final SettingsHandler settings = SettingsHandler.getSettingsHandler();
    private static final String DEFAULT_PROJECT_NAME = "Samples";
    private static final String DEFAULT_PROJECT_PATH = "./samples";
    private static final Logger LOGGER = LogManager.getLogger();
    private static WatchDir watcher;

    @FXML
    private TreeView<Pair<File, String>> trvProjects;

    @FXML
    private Button btnAddFolder;
    @FXML
    private Button btnUpload;

    private final BotFileFilter robotFileFilter = new BotFileFilter();
    private final TreeItem<Pair<File, String>> root = new TreeItem<>(new Pair<>(new File("."), "Projects"));
    private FXController controller;

    // Context menu items.
    private MenuItem menuCut, menuCopy, menuPaste, menuRename, menuDelete;
    private List<File> bulkFiles; // Files to copy or cut.
    private boolean copy = false; // True: copy, false: cut.

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
        trvProjects.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        root.setExpanded(true);

        try {
            watcher = new WatchDir();
            new Thread(watcher).start();
        } catch (IOException e) {
            LOGGER.error("IOException when creating the WatchDir.", e);
        }

        trvProjects.setCellFactory(treeView -> new CustomTreeCell());

        // Add event listeners.
        this.addEventFilter(KeyEvent.KEY_PRESSED, this);
        this.addEventFilter(MouseEvent.MOUSE_PRESSED, this);

        loadProjects();
        addContextMenu();
    }

    private void addContextMenu() {
        // Cut.
        menuCut = new MenuItem("Cut");
        menuCut.setOnAction(e -> cut());

        // Copy.
        menuCopy = new MenuItem("Copy");
        menuCopy.setOnAction(e -> copy());

        // Paste.
        menuPaste = new MenuItem("Paste");
        menuPaste.setOnAction(e -> paste());

        // Rename.
        menuRename = new MenuItem("Rename");
        menuRename.setOnAction(e -> renameButtonPressed());

        // Delete.
        menuDelete = new MenuItem("Delete");
        menuDelete.setOnAction(e -> deleteButtonPressed());

        // Upload.
        MenuItem menuUpload = new MenuItem("Upload");
        menuUpload.setOnAction(e -> uploadButtonPressed());

        // Create the context menu.
        ContextMenu menu = new ContextMenu(menuCut, menuCopy, menuPaste, menuRename, menuDelete, menuUpload);
        trvProjects.setContextMenu(menu);
        // Only paste when there is just 1 item selected (the paste location) and there are files to paste.
        trvProjects.setOnContextMenuRequested(e -> menuPaste.setDisable(getAllCurrentItems().size() != 1 || bulkFiles == null || bulkFiles.isEmpty()));
    }

    /* Bulk file functionality. */

    private void cut() {
        copy = false;
        bulkFiles = getAllCurrentFiles();
    }

    private void copy() {
        copy = true;
        bulkFiles = getAllCurrentFiles();
    }

    private void paste() {
        paste(getCurrentItem().getValue().getKey(), bulkFiles, copy);
        // If the files were moved (not copied), clear the bulk files.
        if (!copy) {
            bulkFiles = null;
        }
    }
    protected void paste(File pasteLoc, List<File> files, boolean copy) {
        // Get the directory to paste in.
        final File destDir = pasteLoc.isDirectory() ? pasteLoc : pasteLoc.getParentFile();

        for (File oldFile : files) {
            // Check if the file already exists.
            File destFile = new File(destDir, oldFile.getName());
            if (destFile.exists()) {
                // Show a dialog.
                AlertDialog dialog = new AlertDialog(Alert.AlertType.ERROR,
                        "File already exists", "",
                        "The destination file (" + destFile.toString() + ") already exists. Press OK to continue or Cancel to abort.",
                        ButtonType.OK, ButtonType.CANCEL);
                final Optional<ButtonType> result = dialog.showAndWait();

                // If cancel was pressed, abort.
                if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                    break;
                }
                continue;
            }

            try {
                // Copy or move the file or directory.
                if (copy) {
                    if (oldFile.isDirectory()) {
                        FileUtils.copyDirectoryToDirectory(oldFile, destDir);
                    } else {
                        FileUtils.copyFileToDirectory(oldFile, destDir);
                    }
                } else {
                    if (oldFile.isDirectory()) {
                        FileUtils.moveDirectoryToDirectory(oldFile, destDir, false);
                    } else {
                        FileUtils.moveFileToDirectory(oldFile, destDir, false);
                    }
                }
            } catch (IOException e) {
                // Show the error.
                LOGGER.error("IOException while moving files.", e);
                AlertDialog error = new AlertDialog(Alert.AlertType.ERROR, "Error while pasting files.", "",
                        "An error occurred while pasting files. Press OK to continue or Cancel to abort.\n" + e.getMessage(),
                        ButtonType.OK, ButtonType.CANCEL);
                final Optional<ButtonType> result = error.showAndWait();

                // If cancel was pressed, abort.
                if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                    break;
                }
            }
        }
    }

    /* End of bulk file functionality. */

    @FXML
    private void newProjectButtonPressed() {
        NewProjectDialog dlg = new NewProjectDialog(this);
        dlg.showAndWait();
    }

    @FXML
    private void newFolderButtonPressed() {
        new NewFolderDialog(this, getCurrentItem()).show();
    }

    @FXML
    private void uploadButtonPressed() {
        new UploadToServerDialog(this, getAllCurrentItems()).show();
    }

    private void renameButtonPressed() {
        RobotTab orgTab = (RobotTab) controller.findTab(getCurrentItem().getValue().getKey());

        // Test if robot is still running
        if (orgTab != null && orgTab.getEditorPane().getControls().robotRunning()) {
            AlertDialog dialog = new AlertDialog(Alert.AlertType.WARNING,
                    "Rename running robot",
                    "You are trying to rename running robot!",
                    "Do you want to stop the robot so you can rename it?",
                    ButtonType.YES, ButtonType.NO
            );
            final Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                orgTab.getEditorPane().getControls().stop();// Stop robot before renaming
            } else {
                return;
            }
        }

        String oldName = getCurrentItem().getValue().getValue();
        RenameDialog dlg = new RenameDialog(getCurrentItem());
        dlg.showAndWait();
        String newName = getCurrentItem().getValue().getValue();
        if (!oldName.equals(newName)) {// name has changed
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

    private void deleteButtonPressed() {
        ObservableList<TreeItem<Pair<File, String>>> selectedItems = getAllCurrentItems();

        // First check if there are any robots running, and count the amount of projects and robot files.
        boolean running = checkRobotsRunning(selectedItems, false);
        int robotFiles = 0;
        int projects = 0;
        for (TreeItem<Pair<File, String>> item : selectedItems) {
            if (item == getProject(item)) {
                projects++;
            } else {
                robotFiles++;
            }
        }

        // Build the title text for the confirmation dialog.
        String titleText = "Deleting "
                + (robotFiles > 0 ? robotFiles + " robot(s)" : "")
                + (robotFiles > 0 && projects > 0 ? " and " : "")
                + (projects > 0 ? projects + " project(s)." : ".");

        // Create and show the dialog.
        AlertDialog dialog = new AlertDialog(Alert.AlertType.WARNING,
                titleText,
                running ? "One or more robots are still running, deleting will terminate them." : "",
                "Do you want to delete all selected items from your drive?",
                ButtonType.YES, ButtonType.NO
        );
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            // If there are projects to be deleted, ask whether to remove them or delete them on disk.
            boolean hardDelete = false;
            if (projects > 0) {
                AlertDialog projectDelete = new AlertDialog(Alert.AlertType.WARNING,
                        "Delete projects from disk?", "",
                        "Deleting projects will remove them from your workspace. Do you also want to delete all files from your disk?",
                        ButtonType.YES, ButtonType.NO, ButtonType.CANCEL
                );

                Optional<ButtonType> res = projectDelete.showAndWait();
                if (res.isPresent() && res.get() == ButtonType.YES) {
                    hardDelete = true;
                } else if (res.isPresent() && res.get() == ButtonType.CANCEL) {
                    return;
                }
            }

            // Delete the items.
            deleteItems(selectedItems, hardDelete);
        }
    }

    @Override
    public void handle(Event event) {
        // Request focus on mouse press.
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            this.requestFocus();
        }

        // Key presses.
        if (event.getEventType() == KeyEvent.KEY_PRESSED) {
            KeyEvent keyEvent = (KeyEvent) event;

            // Hotkeys.
            HotkeysHandler.Hotkeys hk = FXController.hotkeys.getHotkey(keyEvent);
            if (hk != null) {
                switch (hk) {
                    case CUT:
                        if (!menuCut.isDisable()) {
                            cut();
                        }
                        break;
                    case COPY:
                        if (!menuCopy.isDisable()) {
                            copy();
                        }
                        break;
                    case PASTE:
                        if (!menuPaste.isDisable()) {
                            paste();
                        }
                        break;
                    case RENAME:
                        if (!menuRename.isDisable()) {
                            renameButtonPressed();
                        }
                        break;
                }
            }

            // Keypresses.
            if (keyEvent.getCode() == KeyCode.DELETE && !menuDelete.isDisable()) {
                deleteButtonPressed();
            }
        }
    }

    /**
     * Check if there are any robots running.
     *
     * @param items The items to check.
     * @param stop Whether to stop the running robots.
     * @return True if any items or sub-items are running robots.
     */
    private boolean checkRobotsRunning(List<TreeItem<Pair<File, String>>> items, boolean stop) {
        boolean running = false;

        for (TreeItem<Pair<File, String>> item : items) {
            if (item != null && item == getProject(item)) {
                // If the item is a project, recursively check all sub-items.
                running |= checkRobotsRunning(item.getChildren(), stop);
            } else {
                // Check if the robot tab is open and the robot is running.
                RobotTab tab = (RobotTab) controller.findTab(item.getValue().getKey());
                if (tab != null && tab.getEditorPane().getControls().robotRunning()) {
                    running = true;
                    // Stop the robot.
                    if (stop) {
                        tab.getEditorPane().getControls().stop();
                    }
                }
            }
        }

        return running;
    }

    /**
     * Delete multiple items from the tree view.
     *
     * @param items              The items to delete.
     * @param hardDeleteProjects Whether to delete the projects from disk.
     */
    private void deleteItems(ObservableList<TreeItem<Pair<File, String>>> items, boolean hardDeleteProjects) {
        // Stop and delete all folders and robots, close tabs from deleted robots.
        checkRobotsRunning(items, true);
        items.stream().forEach(t -> {
            File f = t.getValue().getKey();
            controller.closeTab(controller.findTab(f));

            // Recursively delete all children.
            deleteItems(t.getChildren(), hardDeleteProjects);

            // Delete the file or directory.
            try {
                if (f.isDirectory()) {
                    FileUtils.deleteDirectory(f);
                } else {
                    f.delete();
                }
            } catch (IOException e) {
                LOGGER.error("Could not delete " + f.toString(), e);
            }
        });

        // Delete all projects.
        items.stream().filter(i -> i != null && i == getProject(i))
                .forEach(p -> {
                    if (hardDeleteProjects) {
                        deleteProject(p);
                    } else {
                        removeProject(p);
                    }
                });
    }

    /* Projects */

    private void loadProjects() {
        Platform.runLater(() -> {
            List<ProjectSettings> projects = settings.project().getAll();
            if (projects.isEmpty()) {
                disableAllButtons(true);
                return;
            }

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
     * @param item the item to remove
     */
    public void removeProject(final TreeItem<Pair<File, String>> item) {
        root.getChildren().remove(item);
        settings.project().delete(item.getValue().getValue());
        if (getProjectsCount() == 0) {
            disableFileButtons(true);
            disableAllButtons(true);
        }
    }

    /**
     * Deletes a project and it's files.
     *
     * @param item a project item
     */
    public void deleteProject(final TreeItem<Pair<File, String>> item) {
        try {
            FileUtils.deleteDirectory(item.getValue().getKey());
        } catch (IOException e) {
            LOGGER.error("Failed to delete project", e);
        }
        removeProject(item);
    }

    /**
     * Creates a new project.
     *
     * @param name        the name of the new project
     * @param folder      the folder representing the project
     * @param description the description of the project
     * @return whether creating the project was successful
     */
    public boolean newProject(final String name, final String folder, final String description) {
        boolean projectDoesntExist = root.getChildren().parallelStream().map(TreeItem::getValue).map(Pair::getValue).noneMatch(n -> n.equalsIgnoreCase(name))
                && findItemByPath(root, folder) == null;
        if (projectDoesntExist) {
            ProjectSettings project = new ProjectSettings(name, folder, description);
            settings.project().save(project);
            try {
                FileUtils.forceMkdir(new File(project.getFolder()));
            } catch (IOException e) {
                LOGGER.error("Failed to create project directory", e);
            }
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
            } catch (IOException e) {
                LOGGER.error("IOException while adding a folder listener.", e);
            }
        }
        projectNode.setExpanded(false);
        select(projectNode);
    }

    /* End of projects */

    /* Selection of TreeItems */

    /**
     * Selects an item in the treeview corresponding to given a path.
     *
     * @param path The path of the item to select.
     */
    public void select(final String path) {
        select(path != null ? findItemByPath(root, path) : null);
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
     * Select an item from the treeview.
     *
     * @param item The item to select.
     */
    public void select(final TreeItem<Pair<File, String>> item) {
        trvProjects.getSelectionModel().clearSelection();
        if (item != null) {
            trvProjects.getSelectionModel().select(item);
        }
    }

    /* End of selection. */

    /**
     * Called when the outside change to the robot file has been done
     * Check if the outside change to the robot file should lead to asking user about loading new content and if so then do it
     *
     * @param child The path to the robot file
     */
    private void robotFileChanged(final Path child) {

        File file = child.toFile();
        RobotTab tab = (RobotTab) controller.findTab(file);
        if (tab == null) {
            return;
        }

        // Test of content change
        String newContent = "";
        try {
            newContent = new String(Files.readAllBytes(child));
        } catch (IOException e) {
            LOGGER.warn("Failed to read changed robot content", e);
        }

        if (!tab.getEditorPane().checkChangedCode(newContent)) {
            return;
        }

        tab.requestFocus();

        // This must be done in the FX application thread.
        final Runnable showDialog = () -> {
            // Create and show an alert dialog saying the content has been changed.
            AlertDialog alert = new AlertDialog(Alert.AlertType.WARNING, "Robot file content change",
                    "The robot file has been modified outside the editor.", "Do you want reload the robot file?",
                    ButtonType.YES, ButtonType.NO);

            final Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.YES) {
                tab.reload();
            }
        };
        Platform.runLater(showDialog);
    }

    @Override
    public void folderChanged(final Path dir, final Path child, final WatchEvent<Path> event) {
        for (TreeItem<Pair<File, String>> item : root.getChildren()) {
            if (item instanceof ProjectTreeItem) {
                ProjectTreeItem project = (ProjectTreeItem) item;
                if (dir.startsWith(project.getValue().getKey().getAbsolutePath())) {
                    if (event.kind() == ENTRY_MODIFY) {
                        // The content of file in project directory has been changed
                        robotFileChanged(child);
                    } else { // ENTRY_CREATE
                        // The files in project directory has changed (i.e. some file(s) has been removed / renamed / added)
                        project.refresh();
                    }
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
     * @return All currently selected nodes.
     */
    public ObservableList<TreeItem<Pair<File, String>>> getAllCurrentItems() {
        return trvProjects.getSelectionModel().getSelectedItems();
    }

    public List<File> getAllCurrentFiles() {
        return getAllCurrentItems().stream().map(t -> t.getValue().getKey()).collect(Collectors.toList());
    }

    /**
     * Gets the first project node above the childItem.
     *
     * @param childItem The childItem to find the project for.
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
     * @param file The file to get the project path for.
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

    /**
     * This method is called when the selection in the tree view is changed
     *
     * @see javafx.beans.value.ChangeListener#changed(javafx.beans.value.ObservableValue, java.lang.Object, java.lang.Object)
     */
    @Override
    public void changed(final ObservableValue<? extends TreeItem<Pair<File, String>>> arg0, final TreeItem<Pair<File, String>> oldObject, final TreeItem<Pair<File, String>> newObject) {
        // Update all buttons to their default state.
        disableAllButtons(false);
        disableFileButtons(false);

        if (newObject == null) {
            // No item is selected.
            disableAllButtons(true);
            disableFileButtons(true);
        } else if (newObject == getProject(newObject)) {
            // This is a project.
            menuRename.setDisable(true);
        }

        // Check if more than 1 item is selected.
        if (getAllCurrentItems().size() > 1) {
            menuRename.setDisable(true);
            btnAddFolder.setDisable(true);
            disableFileButtons(true);
        }

        // If a project is selected disable the cut menu item.
        menuCut.setDisable(false);
        getAllCurrentItems().forEach(i -> {
            if (i != null && i == getProject(i)) {
                menuCut.setDisable(true);
            }
        });
    }

    /**
     * Enable or disable all buttons.
     *
     * @param disable Whether to disable or enable the buttons.
     */
    private void disableAllButtons(boolean disable) {
        btnAddFolder.setDisable(disable);
        btnUpload.setDisable(disable);
        menuDelete.setDisable(disable);
        menuRename.setDisable(disable);
    }

    /**
     * Update the New File and Open File buttons.
     *
     * @param disable Whether to disable or enable the buttons.
     */
    private void disableFileButtons(boolean disable) {
        controller.disableNewFileButton(disable);
        controller.disableOpenFileButton(disable);
    }

    /**
     * Get the number of projects
     *
     * @return the number of projects present
     */
    public int getProjectsCount() {
        return settings.project().getAll().size();
    }

    /**
     * A custom tree cell which opens a robot tab on double-click and supports drag&drop.
     */
    private class CustomTreeCell extends TreeCell<Pair<File, String>> implements EventHandler<Event> {
        private final String dragOverClass = "drag-over";

        public CustomTreeCell() {
            // Subscribe to events.
            this.setOnDragDetected(this);
            this.setOnDragOver(this);
            this.setOnDragDropped(this);
            this.setOnDragEntered(this);
            this.setOnDragExited(this);
        }

        @Override
        protected void updateItem(final Pair<File, String> pair, final boolean empty) {
            super.updateItem(pair, empty);

            // Check if the pair or the string is null, set the text.
            if (pair == null || pair.getValue() == null) {
                setText("");
                return;
            }
            this.setText(pair.getValue());

            setOnMouseClicked(event -> {
                // Double click.
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() > 1) {
                    if (pair.getKey() != null && pair.getKey().exists() && pair.getKey().isFile()) {
                        // Open new tab from file.
                        controller.openFile(pair.getKey());
                    }
                }
            });
        }

        @Override
        public void handle(Event event) {
            if (event instanceof MouseEvent) {
                MouseEvent mouseEvent = (MouseEvent) event;

                if (mouseEvent.getEventType() == MouseEvent.DRAG_DETECTED) {
                    // Start dragging.
                    Dragboard board = this.startDragAndDrop(TransferMode.MOVE);

                    // Set the clipboard content.
                    ClipboardContent content = new ClipboardContent();
                    content.putFiles(getAllCurrentFiles());
                    board.setContent(content);

                    event.consume();
                }
            } else if (event instanceof DragEvent) {
                DragEvent dragEvent = (DragEvent) event;

                if (dragEvent.getEventType() == DragEvent.DRAG_OVER) {
                    // Dragging over.
                    dragEvent.acceptTransferModes(TransferMode.MOVE);
                } else if (dragEvent.getEventType() == DragEvent.DRAG_ENTERED) {
                    this.getStyleClass().add(dragOverClass);
                } else if (dragEvent.getEventType() == DragEvent.DRAG_EXITED) {
                    this.getStyleClass().remove(dragOverClass);
                } else if (dragEvent.getEventType() == DragEvent.DRAG_DROPPED) {
                    // Dropping.
                    Dragboard board = dragEvent.getDragboard();
                    if (board.hasFiles()) {
                        paste(this.getItem().getKey(), board.getFiles(), false);
                        dragEvent.setDropCompleted(true);
                    }

                    event.consume();
                }
            }
        }
    }
}
