package nl.xillio.xill.plugins.selenium;

import org.openqa.selenium.WebElement;
import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

public class InputConstruct implements Construct {

	@Override
	public String getName() {
		return "input";
	}

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(
			InputConstruct::process,
			new Argument("element"),
			new Argument("text"));
	}

	public static MetaExpression process(final MetaExpression elementVar, final MetaExpression textVar) {
		
		if (!NodeVariable.checkType(elementVar)) {
			throw new RobotRuntimeException("Invalid variable type. NODE type expected!");
		}
		//else
		
		String text = textVar.getStringValue();

		WebElement element = NodeVariable.get(elementVar);
				
		try {
			element.clear();
			element.sendKeys(text);
		} catch (Exception e) {
			throw new RobotRuntimeException(e.getClass().getSimpleName(), e);
		}

		return ExpressionBuilder.NULL;
	}

}