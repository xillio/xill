package nl.xillio.xill.plugins.rest.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.rest.data.Content;
import nl.xillio.xill.plugins.rest.data.Options;
import nl.xillio.xill.plugins.rest.services.RestService;

/**
 * Returns content of GET Rest command
 *
 * @author Zbynek Hochmann
 */
public class GetConstruct extends Construct {

	@Inject
	private RestService restService;

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(
			(url, options, body) -> process(url, options, body, restService),
				new Argument("url", ATOMIC),
				new Argument("options", NULL, OBJECT),
				new Argument("body", NULL, LIST, OBJECT, ATOMIC)
		);
	}

	static MetaExpression process(final MetaExpression urlVar, final MetaExpression optionsVar, final MetaExpression bodyVar, final RestService service) {
		String url = urlVar.getStringValue();
		Options options = new Options(optionsVar);
		Content body = new Content(bodyVar);
		return service.get(url, options, body).getMeta();
	}
}
