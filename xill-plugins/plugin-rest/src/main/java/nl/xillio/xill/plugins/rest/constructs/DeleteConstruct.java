package nl.xillio.xill.plugins.rest.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.rest.data.Options;
import nl.xillio.xill.plugins.rest.services.RestService;

/**
 * Returns content of DELETE Rest command
 *
 * @author Zbynek Hochmann
 */
public class DeleteConstruct extends Construct {

	@Inject
	private RestService restService;

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(
			(url, options) -> process(url, options, restService),
				new Argument("url", ATOMIC),
				new Argument("options", NULL, OBJECT)
		);
	}

	static MetaExpression process(final MetaExpression urlVar, final MetaExpression optionsVar, final RestService service) {
		String url = urlVar.getStringValue();
		Options options = new Options(optionsVar);
		return service.delete(url, options).getMeta();
	}
}
