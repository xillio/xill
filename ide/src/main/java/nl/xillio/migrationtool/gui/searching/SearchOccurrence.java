package nl.xillio.migrationtool.gui.searching;

/**
 * @author Thomas Biesaart
 *         This little class is used to represent search occurrences in the UI
 */
public class SearchOccurrence {
	private final int start;
	private final String match;
	private final int end;

	public SearchOccurrence(final int start, final String match) {
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
