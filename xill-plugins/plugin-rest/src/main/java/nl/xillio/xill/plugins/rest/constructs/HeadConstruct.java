package nl.xillio.xill.plugins.rest.constructs;

import com.google.inject.Inject;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.rest.data.Options;
import nl.xillio.xill.plugins.rest.services.RestService;

/**
 * Returns content of HEAD Rest command
 *
 * @author Zbynek Hochmann
 */
public class HeadConstruct extends Construct {

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
		if (url.isEmpty()) {
			throw new RobotRuntimeException("URL is empty!");
		}
		Options options = new Options(optionsVar);
		return service.head(url, options).getMeta();
	}
}
