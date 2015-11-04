package nl.xillio.xill.plugins.web.constructs;

import java.util.List;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.PhantomJSConstruct;
import nl.xillio.xill.plugins.web.data.WebVariable;
import nl.xillio.xill.plugins.web.services.web.WebService;

import com.google.inject.Inject;

/**
 * Gets the text content from provided web element.
 */
public class GetTextConstruct extends PhantomJSConstruct {

	@Inject
	private WebService webService;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			element -> process(element, webService),
			new Argument("element", LIST, ATOMIC));
	}

	/**
	 * @param elementVar
	 *        input variable (should be of a NODE type or list of NODE type variables)
	 * @param webService
	 *        the service we're using.
	 * @return string variable that contains the text(s) of the provided web element(s)
	 */
	public static MetaExpression process(final MetaExpression elementVar, final WebService webService) {
		assertNotNull(elementVar, "element");

		String output = "";
		if (elementVar.getType() == LIST) {
			@SuppressWarnings("unchecked")
			List<MetaExpression> list = (List<MetaExpression>) elementVar.getValue();
			for (MetaExpression item : list) {
				output += processItem(item, webService);
			}
		} else {
			output = processItem(elementVar, webService);
		}

		return fromValue(output);
	}

	private static String processItem(final MetaExpression var, final WebService webService) {
		WebVariable element;
		if (checkNodeType(var)) {
			element = getNode(var);
		} else if (checkPageType(var)) {
			element = getPage(var);
		} else {
			throw new RobotRuntimeException("Invalid variable type. NODE or PAGE expected.");
		}

		String text;
		if (!checkPageType(var) && ("input".equals(webService.getTagName(element)) || "textarea".equals(webService.getTagName(element)))) {
			text = webService.getAttribute(element, "value");
		} else {
			text = webService.getText(element);
		}
		return text;
	}
}
