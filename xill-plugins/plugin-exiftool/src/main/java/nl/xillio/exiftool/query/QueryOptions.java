package nl.xillio.exiftool.query;

/**
 * This interface represents the base class for options for queries.
 *
 * @author Thomas Biesaart
 */
public interface QueryOptions {

    TagNameConvention getTagNameConvention();

    void setTagNameConvention(TagNameConvention tagNameConvention);
}
