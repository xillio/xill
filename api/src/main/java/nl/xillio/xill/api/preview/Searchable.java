package nl.xillio.xill.api.preview;

/**
 * An object of which content can be searched.
 */
public interface Searchable {

    /**
     * Searches the object using a regular expression.
     *
     * @param pattern       regular expression to search the component against
     * @param caseSensitive whether the search should take case into account
     */
    void searchPattern(String pattern, boolean caseSensitive);

    /**
     * Searches the object for occurrences of the needle.
     *
     * @param needle        the text to search for
     * @param caseSensitive whether the search should take case into account
     */
    void search(String needle, boolean caseSensitive);

    /**
     * Returns the number of occurrences.
     *
     * @return the number of occurrences
     */
    int getOccurrences();

    /**
     * Finds the next occurrence.
     *
     * @param next the zero-based index of the next occurrence.
     */
    void findNext(int next);

    /**
     * Finds the previous occurrence.
     *
     * @param previous the zero-based index of the previous occurrence.
     */
    void findPrevious(int previous);

    /**
     * Clears the search and any highlights.
     */
    void clearSearch();
}
