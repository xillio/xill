package nl.xillio.xill.plugins.list.constructs;

import java.util.List;
import java.util.Map;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 *
 * Removes the element at the given index from the list.
 *
 *
 * @author Sander Visser
 *
 */
public class RemoveConstruct extends Construct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(input, index) -> process(input, index),
			new Argument("list", LIST, OBJECT),
			new Argument("index", ATOMIC));
	}

	static MetaExpression process(final MetaExpression input, final MetaExpression indexVar) {
		if (input.getType() == LIST) {
			@SuppressWarnings("unchecked")
			List<MetaExpression> list = (List<MetaExpression>) input.getValue();

			int i = indexVar.getNumberValue().intValue();
			if (i >= 0 && i < list.size()) {
				list.remove(i).releaseReference();
			} else {
				throw new RobotRuntimeException("Index is out of bounds: " + i);
			}
		} else if (input.getType() == OBJECT) {
			@SuppressWarnings("unchecked")
			Map<String, MetaExpression> object = (Map<String, MetaExpression>) input.getValue();
			if (object.containsKey(indexVar.getStringValue())) {
				object.remove(indexVar.getStringValue());
			}
		}
		return NULL;

	}
}
