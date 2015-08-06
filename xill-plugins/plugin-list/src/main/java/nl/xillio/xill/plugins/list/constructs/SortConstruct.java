package nl.xillio.xill.plugins.list.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.list.services.sort.Sort;

/**
 * returns the sorted list.
 * <p>
 * if recursive is true it will also sort lists inside the list.
 * <p>
 * if onKeys is true it will sort by key.
 *
 * @author Sander Visser
 */
public class SortConstruct extends Construct {

	@Inject
	private Sort sort;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
						(list, recursive, onKeys,reverse) -> process(list, recursive, onKeys,reverse, sort),
						new Argument("list", LIST, OBJECT),
						new Argument("recursive", FALSE, ATOMIC),
						new Argument("onKeys", FALSE, ATOMIC),
				    new Argument("reverse",FALSE,ATOMIC));
	}

	static MetaExpression process(final MetaExpression inputList, final MetaExpression recursive, final MetaExpression onKeys, final MetaExpression reverse, final Sort sort) {

		boolean sortRecursive = recursive.getBooleanValue();
		boolean sortKeys = onKeys.getBooleanValue();
		boolean lowFirst = reverse.getBooleanValue();
		Object obj = extractValue(inputList);
		obj = sort.asSorted(obj, sortRecursive, sortKeys,lowFirst);
		MetaExpression m = MetaExpression.parseObject(obj);
		return m;

	}
}
