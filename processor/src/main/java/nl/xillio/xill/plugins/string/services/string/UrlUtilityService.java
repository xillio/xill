package nl.xillio.xill.plugins.string.services.string;

import com.google.inject.ImplementedBy;
import nl.xillio.xill.plugins.string.StringXillPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.PatternSyntaxException;

/**
 * This interface represents some of the operations for the {@link StringXillPlugin}.
 */
@ImplementedBy(UrlUtilityServiceImpl.class)
public interface UrlUtilityService {

    /**
     * Recieves an URL and returns a cleaned version.
     *
     * @param url The URL to clean.
     * @return The cleaned version of the URL.
     */
    String cleanupUrl(final String url);

    /**
     * Reads a file to a byte array given a filename.
     *
     * @param fileName The name of the file we want to read.
     * @return A bytearray with content.
     * @throws IOException
     */
    byte[] readFileToByteArray(File fileName) throws IOException;

    /**
     * Tries to convert the relativeUrl using the pageUrl
     *
     * @param pageUrl     The pageUrl.
     * @param relativeUrl The relativeUrl.
     * @return A converted relativeUrl as a string.
     * @throws IllegalArgumentException
     * @throws PatternSyntaxException
     */
    String tryConvert(final String pageUrl, final String relativeUrl) throws IllegalArgumentException;

    /**
     * Writes a given output in a file.
     *
     * @param fileName The name of the file.
     * @param output   The output to write.
     * @throws FileNotFoundException
     * @throws IOException
     */
    void write(File fileName, byte[] output) throws IOException;

}