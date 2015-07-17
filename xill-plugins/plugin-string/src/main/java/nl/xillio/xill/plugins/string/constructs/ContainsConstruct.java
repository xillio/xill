package nl.xillio.xill.plugins.string.constructs;

import java.io.InputStream;
import java.util.List;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.construct.HelpComponent;

/**
 * Returns true when the first value contains the second value.
 *
 *
 * @author Sander
 */
public class ContainsConstruct extends Construct implements HelpComponent {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(ContainsConstruct::process, new Argument("haystack"), new Argument("needle"));
	}

	@SuppressWarnings("unchecked")
	private static MetaExpression process(final MetaExpression haystack, final MetaExpression needle) {
		// If either is null then false.
		if (haystack == NULL || needle == NULL) {
			return fromValue(false);
		}

		assertType(needle, "needle", ATOMIC);

		// Compare lists
		if (haystack.getType() == LIST) {
			List<MetaExpression> list = (List<MetaExpression>) haystack.getValue();
			return fromValue(list.contains(needle));
		}

		// Compare strings
		String value1 = haystack.getStringValue();
		String value2 = needle.getStringValue();
		return fromValue(value1.contains(value2));
	}

	@Override
	public InputStream openDocumentationStream() {
		return getClass().getResourceAsStream("/helpfiles/contains.xml");
	}

}
