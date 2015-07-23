package nl.xillio.xill.plugins.list.constructs;

import java.util.List;
import java.util.Map;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;

/**
 *
 * Removes the element at the given index from the list.
 *
 *
 * @author Sander
 *
 */
public class RemoveConstruct extends Construct {

	@Override
	public String getName() {

		return "remove";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(RemoveConstruct::process, new Argument("list"), new Argument("index"));
	}

	private static MetaExpression process(final MetaExpression listVar, final MetaExpression indexVar) {

		if (listVar.getType() == LIST) {
			List<MetaExpression> list = (List<MetaExpression>) listVar.getValue();

			int i = indexVar.getNumberValue().intValue();
			if (i > 0 && i < list.size()) {
				list.remove(i);
				return TRUE;
			}
		} else if (listVar.getType() == OBJECT) {
			Map<String, MetaExpression> object = (Map<String, MetaExpression>) listVar.getValue();
			if (object.containsKey(indexVar.getStringValue())) {
				object.remove(indexVar.getStringValue());
				return TRUE;
			}
		}
		return FALSE;
	}
}
