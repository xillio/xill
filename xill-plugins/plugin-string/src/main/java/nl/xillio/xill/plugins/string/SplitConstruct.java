package nl.xillio.xill.plugins.string;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 *
 *
 * Splits the provided value into a list of strings, based on the provided
 * delimiter. </br>
 * Optionally you can set keepempty to true to keep empty entries.
 *
 * @author Sander
 *
 */
public class SplitConstruct implements Construct {

	@Override
	public String getName() {

		return "split";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(SplitConstruct::process, new Argument("string"), new Argument("delimiter"),
				new Argument("keepempty", ExpressionBuilder.FALSE));
	}

	private static MetaExpression process(final MetaExpression string, final MetaExpression delimiter,
			final MetaExpression keepempty) {

		//The input has to be atomic.
		if (string.getType() != ExpressionDataType.ATOMIC || delimiter.getType() != ExpressionDataType.ATOMIC || keepempty.getType() != ExpressionDataType.ATOMIC) {
			throw new RobotRuntimeException("Expected atomic value.");
		}
		
		if(string == ExpressionBuilder.NULL || delimiter == ExpressionBuilder.NULL || keepempty == ExpressionBuilder.NULL)
			throw new RobotRuntimeException("Input cannot be null.");
		
		boolean keepEmpty = keepempty.getBooleanValue();

		String[] stringArray = string.getStringValue().split(delimiter.getStringValue());

		List<MetaExpression> list = new ArrayList<>();

		Arrays.stream(stringArray).forEach(str -> {
			if (keepEmpty || !str.equals("")) {
				list.add(ExpressionBuilder.fromValue(str));
			}
		});

		return ExpressionBuilder.fromValue(list);
	}
}