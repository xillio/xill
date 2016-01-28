package nl.xillio.xill.api.preview;

/**
 * Interface for an object of which content can be searched.
 */
public interface Searchable {

    /**
     * Search the object using a regular expression.
     *
     * @param pattern       regular expression to search the component against
     * @param caseSensitive whether the search should take case into account
     */
    void searchPattern(String pattern, boolean caseSensitive);

    /**
     * Search the object for occurrences of the needle.
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
     * Find the next occurrence.
     *
     * @param next The zero-based index of the next occurrence.
     */
    void findNext(int next);

    /**
     * Find the previous occurrence.
     *
     * @param previous The zero-based index of the previous occurrence.
     */
    void findPrevious(int previous);

    /**
     * Clear the search and any highlights.
     */
    void clearSearch();
}
