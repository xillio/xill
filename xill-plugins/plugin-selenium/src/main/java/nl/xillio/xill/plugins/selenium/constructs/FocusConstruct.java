package nl.xillio.xill.plugins.selenium.constructs;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.selenium.NodeVariable;

public class FocusConstruct extends Construct {

	@Override
	public String getName() {
		return "focus";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(FocusConstruct::process, new Argument("element"));
	}

	private static MetaExpression process(final MetaExpression elementVar) {

		if (!NodeVariable.checkType(elementVar)) {
			throw new RobotRuntimeException("Invalid variable type. NODE type expected!");
		}
		// else

		WebElement element = NodeVariable.get(elementVar);
		WebDriver page = NodeVariable.getDriver(elementVar);

		try {
			new Actions(page).moveToElement(element).perform();
		} catch (Exception e) {
			throw new RobotRuntimeException(e.getClass().getSimpleName(), e);
		}

		return NULL;
	}
}
