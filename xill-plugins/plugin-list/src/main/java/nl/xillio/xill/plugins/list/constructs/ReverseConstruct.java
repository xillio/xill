package nl.xillio.xill.plugins.list.constructs;

import com.google.inject.Inject;

import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.list.services.reverse.Reverse;

/**
 *<p>
 * returns the reverse of the given {@link ExpressionDataType#LIST} or {@link ExpressionDataType#OBJECT}.
 * </p>
 * <p>
 * If recursive is true it will also reverse lists and objects inside the given list or object.
 *</p>
 *
 * @author Sander Visser
 *
 */
public class ReverseConstruct extends Construct {

	@Inject
	private Reverse reverse;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(list, recursive) -> process(list, recursive, reverse),
			new Argument("list", LIST, OBJECT),
			new Argument("recursive", FALSE, ATOMIC));
	}

	static MetaExpression process(final MetaExpression input, final MetaExpression recursiveVar, final Reverse reverse) {

		boolean reverseRecursive = recursiveVar.getBooleanValue();
		
		Object obj = extractValue(input);
		obj = reverse.asReversed(obj, reverseRecursive);
		MetaExpression m = MetaExpression.parseObject(obj);
		return m;

	}
}
