package nl.xillio.xill.plugins.string.constructs;

import java.security.NoSuchAlgorithmException;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.string.services.string.RegexService;

import com.google.inject.Inject;

/**
 *<p> Returns a MD5 hash of the given variable.</p>
 *
 * @author Sander
 *
 */
public class CreateMD5Construct extends Construct {
	@Inject
	private RegexService regexService;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(value -> process(value, regexService), new Argument("value", ATOMIC));
	}

	@SuppressWarnings("javadoc")
	public static MetaExpression process(final MetaExpression value, final RegexService regexService) {
		assertNotNull(value, "value");
		try {
			return fromValue(regexService.createMD5Construct(value.getStringValue()));
		} catch (NoSuchAlgorithmException e) {
			throw new RobotRuntimeException("No such algorithm");
		}
	}
}
