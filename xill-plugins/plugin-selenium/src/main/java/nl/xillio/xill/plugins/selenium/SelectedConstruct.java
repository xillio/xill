package nl.xillio.xill.plugins.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.WebElement;

import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

public class SelectedConstruct implements Construct {

	@Override
	public String getName() {
		return "selected";
	}

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(
			FocusConstruct::process,
			new Argument("element"));
	}

	public static MetaExpression process(final MetaExpression elementVar) {
		
		if (!NodeVariable.checkType(elementVar)) {
			throw new RobotRuntimeException("Invalid variable type. NODE type expected!");
		}
		//else
		
		WebElement element = NodeVariable.get(elementVar);
				
		try {
			return ExpressionBuilder.fromValue(element.isSelected());
		} catch (Exception e) {
			throw new RobotRuntimeException(e.getClass().getSimpleName(), e);
		}
	}
}