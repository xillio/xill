package nl.xillio.xill.plugins.selenium.constructs;

import java.util.List;

import org.openqa.selenium.WebElement;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.selenium.NodeVariable;
import nl.xillio.xill.plugins.selenium.PageVariable;

public class GetTextConstruct extends Construct {

	@Override
	public String getName() {
		return "getText";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(GetTextConstruct::process, new Argument("element"));
	}

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
