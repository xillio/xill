package nl.xillio.xill.plugins.string.constructs;

import nl.xillio.xill.api.components.AtomicExpression;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.string.services.string.UrlService;

import com.google.inject.Inject;

/**
 * <p>
 * Converts a relative URL string to an absolute URL using a string, pageurl, as base URL.
 * </p>
 */
public class AbsoluteURLConstruct extends Construct {

	@Inject
	private UrlService urlService;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(pageUrl, relativeUrl) -> process(pageUrl, relativeUrl, urlService),
			new Argument("pageUrl", ATOMIC),
			new Argument("relativeUrl", ATOMIC));
	}

	static MetaExpression process(final MetaExpression pageurlVar, final MetaExpression relativeurlVar, final UrlService urlService) {
		String pageurl = pageurlVar.getStringValue().trim();
		String relativeurl = relativeurlVar.getStringValue().trim();

		if (!pageurl.startsWith("http://") && !pageurl.startsWith("https://")) {
			pageurl = "http://" + pageurl;
		}

		if (pageurl.endsWith("/") && relativeurl.isEmpty()) {
			pageurl = pageurl.substring(0, pageurl.length() - 1);
		}

		if (relativeurl.isEmpty()) {
			return new AtomicExpression(urlService.cleanupUrl(pageurl));
		}

		String processed = urlService.tryConvert(pageurl, relativeurl);

		if (processed != null) {
			return new AtomicExpression(processed);
		} else {
			throw new RobotRuntimeException("The page url is invalid.");
		}
	}
}
