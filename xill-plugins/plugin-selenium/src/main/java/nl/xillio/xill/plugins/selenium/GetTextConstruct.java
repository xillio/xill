package nl.xillio.xill.plugins.selenium;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.WebElement;

import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

public class GetTextConstruct implements Construct {

	@Override
	public String getName() {
		return "gettext";
	}

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(
			GetTextConstruct::process,
			new Argument("element"));
	}

	public static MetaExpression process(final MetaExpression elementVar) {
		
		if (elementVar.isNull()) {
			return ExpressionBuilder.NULL;
		}

		String output = "";
		if (elementVar.getType() == ExpressionDataType.LIST) {
			@SuppressWarnings("unchecked")
			List<MetaExpression> list = (List<MetaExpression>) elementVar.getValue();
			for (MetaExpression item : list) {
				output += processItem(item);
			}
		} else {
			output = processItem(elementVar);
		}
		
		return ExpressionBuilder.fromValue(output);
	}
	
	private static String processItem(final MetaExpression var) {
		WebElement element = null;
		if (NodeVariable.checkType(var)) {
			element = NodeVariable.get(var);
		} else if (PageVariable.checkType(var)) {
			element = (WebElement) PageVariable.get(var).getDriver();
		} else {
			throw new RobotRuntimeException("Invalid variable type.");
		}
			
		String text = "";
		if ( (element.getTagName().equals("input")) || (element.getTagName().equals("textarea"))) {
			text = element.getAttribute("value");
		} else {
			text = element.getText();
		}
		return text;
	}
}