package nl.xillio.migrationtool.gui.searching;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Pair;
import nl.xillio.xill.api.preview.Searchable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * This class implements searching mechanism for PreviewPane
 * It can search both in TextArea and treetable
 *
 * @author Zbynek Hochmann
 */
public class PreviewSearch implements Searchable {

    private static final Logger LOGGER = LogManager.getLogger();

    private class SearchTextOccurrence {
        private final int start;
        private final String match;
        private final int end;

        public SearchTextOccurrence(final int start, final String match) {
            this.start = start;
            this.match = match;
            end = start + match.length();
        }

        public int getStart() {
            return start;
        }

        public String getMatch() {
            return match;
        }

        public int getEnd() {
            return end;
        }
    }

    private class SearchTreeOccurrence {
        public SearchTreeOccurrence(TreeItem<Pair<String, Node>> item) {
            this.item = item;
        }

        public TreeItem<Pair<String, Node>> getItem() {
            return item;
        }

        private TreeItem<Pair<String, Node>> item;
    }

    private AnchorPane apnPreviewPane;
    private final List<Object> occurrences = new ArrayList<>(); // List of found occurrences
    private Pattern regexPattern; // Regexp pattern used while searching the needle
    private TreeTableView<Pair<String, Node>> tableView; // treetable instance (null if does not exist)
    private TextArea text; // textarea instance (null if does not exist)

    public PreviewSearch(final AnchorPane apnPreviewPane) {
        this.apnPreviewPane = apnPreviewPane;
    }

    @SuppressWarnings("squid:S1166") // PatternSyntaxException is handled correctly
    @Override
    public void searchPattern(String pattern, boolean caseSensitive) {
        // Clear selection
        clearSearch();

        // Try to compile the pattern, get the matcher
        regexPattern = null;
        try {
            regexPattern = caseSensitive ? Pattern.compile(pattern) : Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        } catch (PatternSyntaxException e) {
            return; // This return in catch (i.e. exception handling missing) is intentionally - if not valid regexp pattern is passed from user, it will do nothing
        }

        occurrences.clear();

        if (apnPreviewPane.getChildren().size() == 0) { // PreviewPane does not contain any preview item at the moment
            return;
        }

        Node node = apnPreviewPane.getChildren().get(0);
        if (node instanceof TextArea) {
            text = (TextArea)node;
            tableView = null;
            searchText(); // Search in textarea
        } else {
            tableView = getTreeTableView(node);
            text = null;
            searchTreeIterate(tableView.getRoot()); // Search in treetable
        }

        Platform.runLater(() -> select(0));
    }

    @SuppressWarnings("unchecked")
    private TreeTableView<Pair<String, Node>> getTreeTableView(final Node node) {
        return (TreeTableView<Pair<String, Node>>) node;
    }

    @Override
    public void search(String needle, boolean caseSensitive) {
        String pattern = Pattern.quote(needle);
        searchPattern(pattern, caseSensitive);
    }

    @Override
    public int getOccurrences() {
        return occurrences.size();
    }

    @Override
    public void findNext(int next) {
        select(next);
    }

    @Override
    public void findPrevious(int previous) {
        select(previous);
    }

    @Override
    public void clearSearch() {
        occurrences.clear();
    }

    private void select(final int occurrence) {
        if (occurrences.size() == 0) {
            return;
        }

        if (text == null) {
            selectTreeItem(occurrence);
        } else {
            selectText(occurrence);
        }
    }

    private void selectText(final int occurrence) {
        if (occurrence >= 0 && occurrence < occurrences.size()) {
            SearchTextOccurrence element = (SearchTextOccurrence)occurrences.get(occurrence);
            text.selectRange(element.getStart(), element.getEnd());
        }
    }

    private void selectTreeItem(final int occurrence) {
        SearchTreeOccurrence treeOccurrence = (SearchTreeOccurrence)occurrences.get(occurrence);

        Node node = apnPreviewPane.getChildren().get(0);
        TreeTableView<Pair<String, Node>> nodeTableView = getTreeTableView(node);

        expandItem(treeOccurrence.getItem());
        nodeTableView.getSelectionModel().select(treeOccurrence.getItem());

        int row = nodeTableView.getRow(treeOccurrence.getItem());
        nodeTableView.scrollTo(row);
    }

    private void expandItem(final TreeItem<Pair<String, Node>> item) {
        item.setExpanded(true);
        TreeItem<Pair<String, Node>> parent = item.getParent();
        if (parent != null) {
            expandItem(parent);
        }
    }

    private void searchText() {
        Matcher matcher = regexPattern.matcher(text.getText());

        // Find all occurrences
        occurrences.clear();
        while (matcher.find()) {
            occurrences.add(new SearchTextOccurrence(matcher.start(), matcher.group()));
        }
    }

    private void searchTreeIterate(TreeItem<Pair<String, Node>> parent) {

        parent.getChildren().forEach(item -> {

            String key = item.getValue().getKey();
            if (regexPattern.matcher(key).find()) {
                occurrences.add(new SearchTreeOccurrence(item));
            } else if (item.isLeaf()) {
                Node node = item.getValue().getValue();
                String value = ((Text) node).getText();

                if (regexPattern.matcher(value).find()) {
                    occurrences.add(new SearchTreeOccurrence(item));
                }
            }

            searchTreeIterate(item);
        });
    }
}
