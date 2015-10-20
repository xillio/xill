package nl.xillio.migrationtool.gui.searching;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javafx.scene.control.TextArea;
import nl.xillio.xill.api.preview.Searchable;

public class SearchTextArea extends TextArea implements Searchable {
	/**
	 * This field contains all occurrences of the search.
	 * They are represented by a Integer location (start) and a String match
	 */
	private final List<SearchOccurrence> occurrences = new ArrayList<>();

	@Override
	public void searchPattern(final String pattern, final boolean caseSensitive) {
		// Clear selection
		clearSearch();

		// Try to compile the pattern, get the matcher
		Pattern regex = null;
		try {
			regex = caseSensitive ? Pattern.compile(pattern) : Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		} catch (PatternSyntaxException e) {
			return;
		}
		Matcher matcher = regex.matcher(getText());

		// Find all occurrences
		occurrences.clear();
		while (matcher.find()) {
			occurrences.add(new SearchOccurrence(matcher.start(), matcher.group()));
		}
	}

	@Override
	public void search(final String needle, final boolean caseSensitive) {
		String pattern = Pattern.quote(needle);
		searchPattern(pattern, caseSensitive);
	}

	@Override
	public int getOccurrences() {
		return occurrences.size();
	}

	@Override
	public void highlight(final int occurrence) {
		SearchOccurrence element = occurrences.get(occurrence);
		selectRange(element.getStart(), element.getEnd());
	}

	@Override
	public void highlightAll() {
		// This is not possible on a textarea
		selectRange(0, 0);
	}

	@Override
	public void clearSearch() {
		selectRange(0, 0);
	}
}
