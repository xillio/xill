package nl.xillio.xill.plugins.web.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.PhantomJSConstruct;
import nl.xillio.xill.plugins.web.data.WebVariable;
import nl.xillio.xill.plugins.web.services.web.WebService;

/**
 * Removes all cookies from a currently loaded page context.
 *
 */
public class RemoveAllCookiesConstruct extends PhantomJSConstruct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			page -> process(page, webService),
			new Argument("page", ATOMIC));
	}

	/**
	 * Tries to delete all cookies from a page in a {@link MetaExpression}.
	 *
	 * @param page
	 *        The {@link MetaExpression} containing the page.
	 * @param webService
	 *        The service we're using.
	 * @return
	 *         Returns NULL.
	 */
	public static MetaExpression process(final MetaExpression page, final WebService webService) {
		
		if(page.isNull()){
			return NULL;
		}

		if (!checkPageType(page)) {
			throw new RobotRuntimeException("Invalid variable type. Node PAGE type expected!");
		}

		WebVariable driver = getPage(page);
		try {
			webService.deleteCookies(driver);
		} catch (Exception e) {
			throw new RobotRuntimeException("Failed to delete all cookies in driver.", e);
		}
		return NULL;
	}

}