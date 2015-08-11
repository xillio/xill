package nl.xillio.xill.plugins.web;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.plugins.web.services.web.WebService;

import org.openqa.selenium.Cookie;

import com.google.inject.Inject;

/**
 * The base class for all the constructs which use PhantomJS.
 *
 */
public abstract class PhantomJSConstruct extends Construct {
	@Inject
	protected WebService webService;

	/**
	 * Creates new {@link NodeVariable}
	 *
	 * @param driver
	 *        PhantomJS driver (page)
	 * @param element
	 *        web element on the page (represented by driver)
	 *
	 * @return created variable
	 */
	protected static MetaExpression createNode(final WebVariable driver, final WebVariable element, final WebService webService) {
		MetaExpression var = fromValue(webService.getAttribute(element, "outerHTML"));
		var.storeMeta(webService.createNodeVariable(driver, element));
		return var;
	}

	/**
	 * Extracts web element from {@link NodeVariable}
	 *
	 * @param var
	 *        input variable (should be of a NODE type)
	 *
	 * @return web element
	 */
	protected static NodeVariable getNode(final MetaExpression var) {
		return var.getMeta(NodeVariable.class);
	}

	/**
	 * Do the test if input {@link MetaExpression} if it's of NODE type
	 *
	 * @param var
	 *        MetaExpression (any variable)
	 *
	 * @return true if it's of NODE type
	 */
	protected static boolean checkNodeType(final MetaExpression var) {
		return var.getMeta(NodeVariable.class) != null;
	}

	/**
	 * Creates new {@link PageVariable}
	 *
	 * @param item
	 *        from PhantomJS pool
	 *
	 * @return created PAGE variable
	 */
	protected static MetaExpression createPage(final WebVariable item, final WebService webService) {
		MetaExpression var = fromValue(webService.getCurrentUrl(item));
		var.storeMeta(item);
		return var;
	}

	/**
	 * Extracts a
	 *
	 * @param var
	 *        input variable (should be of a PAGE type)
	 *
	 * @return driver (page)
	 */
	protected static PageVariable getPage(final MetaExpression var) {
		return var.getMeta(PageVariable.class);
	}

	/**
	 * Do the test if input {@link MetaExpression} if it's of PAGE type
	 *
	 * @param var
	 *        MetaExpression (any variable)
	 *
	 * @return true if it's of PAGE type
	 */
	protected static boolean checkPageType(final MetaExpression var) {
		return var.getMeta(PageVariable.class) != null;
	}

	/**
	 * Creates an associated list variable that contains all information about one cookie
	 *
	 * @param cookie
	 *        Selenium's cookie class
	 *
	 * @return created cookie variable
	 */
	protected static MetaExpression makeCookie(final Cookie cookie, final WebService webService) {
		LinkedHashMap<String, MetaExpression> map = new LinkedHashMap<String, MetaExpression>();
		map.put("name", ExpressionBuilderHelper.fromValue(webService.getName(cookie)));
		map.put("domain", ExpressionBuilderHelper.fromValue(webService.getDomain(cookie)));
		map.put("path", ExpressionBuilderHelper.fromValue(webService.getPath(cookie)));
		map.put("value", ExpressionBuilderHelper.fromValue(webService.getValue(cookie)));

		if (cookie.getExpiry() != null) {
			map.put("expires", ExpressionBuilderHelper.fromValue(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S").format(cookie.getExpiry())));
		}

		return ExpressionBuilderHelper.fromValue(map);
	}
}
