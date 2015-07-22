package nl.xillio.xill.plugins.list.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.list.util.Reverse;

/**
 *
 *
 *
 *
 * @author Sander
 *
 */
public class ReverseConstruct extends Construct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(ReverseConstruct::process, new Argument("list"), new Argument("recursive"));
	}

	private static MetaExpression process(final MetaExpression input, final MetaExpression recursiveVar) {

		boolean reverseRecursive = recursiveVar.getBooleanValue();

		Reverse reverse = new Reverse();
		assertNotType(input, "input", ATOMIC);

		Object obj = extractValue(input);
		reverse.reverted(obj, reverseRecursive);
		MetaExpression m = MetaExpression.parseObject(obj);
		return m;

	}
}
