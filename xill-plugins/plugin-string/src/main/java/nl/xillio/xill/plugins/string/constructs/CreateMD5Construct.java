package nl.xillio.xill.plugins.string.constructs;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * Returns a MD5 hash of the given variable.
 *
 * @author Sander
 *
 */

public class CreateMD5Construct extends Construct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(CreateMD5Construct::process, new Argument("valueVar"));
	}

	private static MetaExpression process(final MetaExpression valueVar) {
		assertType(valueVar, "value", ATOMIC);
		assertNotNull(valueVar, "value");
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(StandardCharsets.UTF_8.encode(valueVar.getStringValue()));
			return fromValue(String.format("%032x", new BigInteger(1, md5.digest())));
		} catch (NoSuchAlgorithmException e) {
			throw new RobotRuntimeException("No such algorithm");
		}
	}

}
