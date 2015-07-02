package nl.xillio.xill.plugins.string;

import java.util.List;

import nl.xillio.xill.api.components.AtomicExpression;
import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * Returns true if the first value contains the second value.
 *
 *
 * @author Sander
 */
public class ContainsConstruct implements Construct {

	@Override
	public String getName() {
		return "contains";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(ContainsConstruct::process, new Argument("haystack"), new Argument("needle"));
	}

	private static MetaExpression process(final MetaExpression haystack, final MetaExpression needle) {
		// If either is null then false.
		if (haystack == ExpressionBuilder.NULL || needle == ExpressionBuilder.NULL) {
			return new AtomicExpression(false);
		}
		// if the needle is not atomic throw an exception.
		if (needle.getType() != ExpressionDataType.ATOMIC) {
			throw new RobotRuntimeException("Second value has to be atomic.");
		}

		// Compare lists
		if (haystack.getType() == ExpressionDataType.LIST) {
			@SuppressWarnings("unchecked")
			List<MetaExpression> list = (List<MetaExpression>) haystack.getValue();
			return new AtomicExpression(list.contains(needle));
		}

		// Compare strings
		String value1 = haystack.getStringValue();
		String value2 = needle.getStringValue();
		return new AtomicExpression(value1.contains(value2));
	}

}
