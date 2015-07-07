package nl.xillio.xill.plugins.string;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.ExpressionDataType;
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

public class MD5Construct implements Construct {

	@Override
	public String getName() {

		return "createMD5";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(MD5Construct::process, new Argument("valueVar"));
	}

	private static MetaExpression process(final MetaExpression valueVar) {

		if (valueVar.getType() != ExpressionDataType.ATOMIC) {
			throw new RobotRuntimeException("Expected atomic value.");
		}
		if (valueVar == ExpressionBuilder.NULL) {
			throw new RobotRuntimeException("Input cannot be null.");

		}
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(StandardCharsets.UTF_8.encode(valueVar.getStringValue()));
			return ExpressionBuilder.fromValue(String.format("%032x", new BigInteger(1, md5.digest())));
		} catch (NoSuchAlgorithmException e) {
			throw new RobotRuntimeException("No such algorithm");
		}
	}

}