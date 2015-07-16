package nl.xillio.xill.plugins.math.constructs;

import java.io.InputStream;
import java.util.List;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.construct.HelpComponent;

/**
 * The construct of the Random function which is capable of generating random
 * numbervalues or getting a random index.
 *
 * @author Ivor
 *
 */
public class RandomConstruct extends Construct implements HelpComponent {

	@Override
	public String getName() {
		return "random";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(RandomConstruct::process, new Argument("value", fromValue(0)));
	}

	private static MetaExpression process(final MetaExpression value) {
		assertNotType(value, "value", OBJECT);

		if (value.getType() == LIST) {
			@SuppressWarnings("unchecked")
			List<MetaExpression> list = (List<MetaExpression>) value.getValue();
			int size = list.size();

			if (size == 0) {
				return NULL;
			}

			int index = (int) (Math.random() * size);
			return list.get(index);
		}
		int intValue = value.getNumberValue().intValue();

		if (intValue <= 0) {
			return fromValue(Math.random());
		}
		return fromValue((int) (Math.random() * intValue));

	}

	@Override
	public InputStream openDocumentationStream() {
		return getClass().getResourceAsStream("/helpfiles/random.xml");
	}

}
