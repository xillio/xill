package nl.xillio.xill.api.preview;

/**
 * An object which content can support search and replace operations.
 */
public interface Replaceable extends Searchable {

    /**
     * Replaces all occurrences of the search expression with the given replacement.
     *
     * @param replacement the replacement string
     */
    public void replaceAll(String replacement);

    /**
     * Replace a given occurrence of a search expression with the given replacement.
     *
     * @param occurrence  the occurrence to be replaced
     * @param replacement the replacement string
     */
    public void replaceOne(int occurrence, String replacement);

}
