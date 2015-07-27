package nl.xillio.xill.plugins.web.constructs;

import java.util.List;

import org.openqa.selenium.WebElement;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.NodeVariable;
import nl.xillio.xill.plugins.web.PageVariable;

/**
 * Gets the text content from provided web element 
 */
public class GetTextConstruct extends Construct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(GetTextConstruct::process, new Argument("element"));
	}

	/**
	 * @param elementVar
	 * 				input variable (should be of a NODE type or list of NODE type variables)
	 * @return string variable that contains the text(s) of the provided web element(s)
	 */
	public static MetaExpression process(final MetaExpression elementVar) {

		assertNotNull(elementVar, "element");

		String output = "";
		if (elementVar.getType() == LIST) {
			@SuppressWarnings("unchecked")
			List<MetaExpression> list = (List<MetaExpression>) elementVar.getValue();
			for (MetaExpression item : list) {
				output += processItem(item);
			}
		} else {
			output = processItem(elementVar);
		}

		return fromValue(output);
	}

	private static String processItem(final MetaExpression var) {
		WebElement element = null;
		if (NodeVariable.checkType(var)) {
			element = NodeVariable.get(var);
		} else if (PageVariable.checkType(var)) {
			element = (WebElement) PageVariable.getDriver(var);
		} else {
			throw new RobotRuntimeException("Invalid variable type.");
		}

		String text = "";
		if (element.getTagName().equals("input") || element.getTagName().equals("textarea")) {
			text = element.getAttribute("value");
		} else {
			text = element.getText();
		}
		return text;
	}
}
