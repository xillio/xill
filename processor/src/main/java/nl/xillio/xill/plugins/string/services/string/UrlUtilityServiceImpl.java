package nl.xillio.xill.plugins.string.services.string;

import com.google.inject.Singleton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is the main implementation of the {@link UrlUtilityService}
 */
@Singleton
public class UrlUtilityServiceImpl implements UrlUtilityService {

    private static String getParentUrl(final String pageurl, String relativeurl) {
        if ("..".equals(relativeurl)) {
            relativeurl = "../";
        }

        if (relativeurl.startsWith("../")) {
            Matcher m = Pattern.compile("(http(s)?://.*)(/[^/]*/[^/]*)").matcher(pageurl);
            if (m.matches()) {
                String parenturl = m.group(1);
                return getParentUrl(parenturl + "/", relativeurl.substring(3));
            }
            return null;
        }
        return pageurl + relativeurl;
    }

    @Override
    public String cleanupUrl(final String url) {
        String cleaned = url.replace("/./", "/");
        return cleaned.replaceAll("/[^/]*/\\.\\./", "/");
    }

    @Override
    public byte[] readFileToByteArray(final Path filePath) throws IOException {
        return Files.readAllBytes(filePath);
    }

    @Override
    public String tryConvert(final String pageUrl, final String relativeUrl) throws IllegalArgumentException {
        if (relativeUrl.startsWith("http://") || relativeUrl.startsWith("https://")) {
            return cleanupUrl(relativeUrl);
        } else if (relativeUrl.matches("//w+.*")) {
            Matcher m = Pattern.compile("(https?:).*").matcher(pageUrl);
            if (m.matches()) {
                String protocol = m.group(1);
                return cleanupUrl(protocol + relativeUrl);
            }
            return cleanupUrl(relativeUrl);
        } else if (relativeUrl.matches("www(\\.\\w+){2,}.*")) {
            return cleanupUrl("http://" + relativeUrl);
        } else if (relativeUrl.startsWith("/")) {
            Matcher m = Pattern.compile("(http(s)?://[^/]*)(/.*)?").matcher(pageUrl);
            if (m.matches()) {
                String baseurl = m.group(1);
                return cleanupUrl(baseurl + relativeUrl);
            }
        } else if (relativeUrl.startsWith("../") || "..".equals(relativeUrl)) {

            String parentUrl = getParentUrl(pageUrl, relativeUrl);
            if (parentUrl != null) {
                return cleanupUrl(parentUrl);
            }
        } else {
            Matcher m = Pattern.compile("(http(s)?://.*)(/[^/]*\\.(htm|html|jsp|php|asp|aspx|shtml|py|cgi|pl|cfm|jspx|php4|php3|rb|rhtml|dll|xml|xhtml|asx|do)[?/]?.*)").matcher(pageUrl);
            if (m.matches()) {
                // Reference to a page
                String parenturl = m.group(1);
                return cleanupUrl(parenturl + "/" + relativeUrl);
            }
            // Reference to a folder
            String parenturl = pageUrl;
            if (!parenturl.endsWith("/")) {
                parenturl = parenturl + "/";
            }
            return cleanupUrl(parenturl + relativeUrl);
        }
        return null;
    }

    @Override
    public void write(final File file, final byte[] output) throws IOException {
        OutputStream out = new FileOutputStream(file);
        out.write(output);
        out.close();
    }
}
