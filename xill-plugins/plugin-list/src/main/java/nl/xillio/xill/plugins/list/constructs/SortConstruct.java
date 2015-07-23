package nl.xillio.xill.plugins.list.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.list.util.Sort;

/**
 *
 * returns the sorted list.
 * if recursive is true it will also sort lists inside the list.
 * if onKeys is true it will sort by key.
 *
 *
 * @author Sander
 *
 */
public class SortConstruct extends Construct {

	@Override
	public String getName() {

		return "sort";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(SortConstruct::process, new Argument("list"), new Argument("recursive", FALSE), new Argument("onKeys", FALSE));
	}

	private static MetaExpression process(final MetaExpression inputList, final MetaExpression recursive, final MetaExpression onKeys) {

		boolean sortKeys = onKeys.getBooleanValue();
		boolean sortRecursive = recursive.getBooleanValue();
		Sort sort = new Sort();
		assertNotType(inputList, "input", ATOMIC);
		Object obj = extractValue(inputList);
		sort.doSorting(obj, sortRecursive, sortKeys);
		MetaExpression m = MetaExpression.parseObject(obj);
		return m;

	}
}
