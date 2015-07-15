package nl.xillio.xill.plugins.string.constructs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.xillio.xill.api.components.AtomicExpression;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 *
 */
public class AbsoluteURLConstruct extends Construct {

    @Override
    public String getName() {
	return "absoluteurl";
    }

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
	return new ConstructProcessor(AbsoluteURLConstruct::process, new Argument("pageurl"), new Argument("relativeurl"));
    }

    private static MetaExpression process(final MetaExpression pageurlVar, final MetaExpression relativeurlVar) {
	String pageurl = pageurlVar.getStringValue().trim();
	String relativeurl = relativeurlVar.getStringValue().trim();

	if (!pageurl.startsWith("http://") && !pageurl.startsWith("https://")) {
	    pageurl = "http://" + pageurl;
	}

	if (relativeurl.isEmpty()) {
	    return new AtomicExpression(cleanupUrl(pageurl));
	}

	if (pageurl.endsWith("/") && relativeurl.isEmpty()) {
	    pageurl = pageurl.substring(0, pageurl.length() - 2);
	}

	try {
	    if (relativeurl.startsWith("http://") || relativeurl.startsWith("https://")) {
		return new AtomicExpression(cleanupUrl(relativeurl));
	    } else if (relativeurl.matches("//w+.*")) {
		Matcher m = Pattern.compile("(https?:).*").matcher(pageurl);
		if (m.matches()) {
		    String protocol = m.group(1);
		    return new AtomicExpression(cleanupUrl(protocol + relativeurl));
		}
		return new AtomicExpression(cleanupUrl(relativeurl));
	    } else if (relativeurl.matches("www(\\.\\w+){2,}.*")) {
		return new AtomicExpression(cleanupUrl("http://" + relativeurl));
	    } else if (relativeurl.startsWith("/")) {
		Matcher m = Pattern.compile("(http(s)?://[^/]*)(/.*)?").matcher(pageurl);
		if (m.matches()) {
		    String baseurl = m.group(1);
		    return new AtomicExpression(cleanupUrl(baseurl + relativeurl));
		}
	    } else if (relativeurl.startsWith("../") || relativeurl.equals("..")) {

		String parenturl = getParentUrl(pageurl, relativeurl);
		if (parenturl != null) {
		    return new AtomicExpression(cleanupUrl(parenturl));
		}
	    } else {
		Matcher m = Pattern.compile("(http(s)?://.*)(/[^/]*\\.(htm|html|jsp|php|asp|aspx|shtml|py|cgi|pl|cfm|jspx|php4|php3|rb|rhtml|dll|xml|xhtml|asx|do)[?/]?.*)").matcher(pageurl);
		if (m.matches()) {
		    // Reference to a page
		    String parenturl = m.group(1);
		    return new AtomicExpression(cleanupUrl(parenturl + "/" + relativeurl));
		}
		// Reference to a folder
		String parenturl = pageurl;
		if (!parenturl.endsWith("/")) {
		    parenturl = parenturl + "/";
		}
		return new AtomicExpression(cleanupUrl(parenturl + relativeurl));
	    }
	} catch (Exception e) {
	    throw new RobotRuntimeException("Invalid URL: Relative url: " + relativeurl + "\nAbsolute url: " + pageurl, e);
	}
	throw new RobotRuntimeException("The page url is invalid.");
    }

    private static String getParentUrl(final String pageurl, String relativeurl) {
	if (relativeurl.equals("..")) {
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

    private static String cleanupUrl(final String url) {
	String cleaned = url.replace("/./", "/");
	cleaned = cleaned.replaceAll("/[^/]*/\\.\\./", "/");
	return cleaned;
    }

}
