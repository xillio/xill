package nl.xillio.xill.plugins.selenium;

import org.openqa.selenium.WebDriver;
import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

public class SwitchFrameConstruct implements Construct {

	@Override
	public String getName() {
		return "switchframe";
	}

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(
			SwitchFrameConstruct::process,
			new Argument("page"),
			new Argument("frame"));
	}

	public static MetaExpression process(final MetaExpression pageVar, final MetaExpression frameVar) {
		
		if (!PageVariable.checkType(pageVar)) {
			throw new RobotRuntimeException("Invalid variable type. Page NODE type expected!");
		}
		//else
		
		WebDriver driver = PageVariable.get(pageVar).getDriver();
		
		try {
			if (NodeVariable.checkType(frameVar)) {
				driver.switchTo().frame(NodeVariable.get(frameVar));
			} else {
				Object frame = MetaExpression.extractValue(frameVar);
				if (frame instanceof Double) {
					Double number = (Double) frame;
					driver.switchTo().frame(number.intValue());
				} else if (frame instanceof String) {
					driver.switchTo().frame(frame.toString());
				} else {
					throw new RobotRuntimeException("Invalid variable type of frame parameter!");
				}
			}
		} catch (Exception e) {
			throw new RobotRuntimeException(e.getClass().getSimpleName(), e);
		}
	
		return ExpressionBuilder.NULL;
	}

}