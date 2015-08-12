package nl.xillio.xill.plugins.web.data;

import java.util.Map;

import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * The factory for the {@link Options} variable.
 *
 */
public class OptionsFactory {

	/**
	 * Processes the Proxy option given an object with options.
	 *
	 * @param optionsParameters
	 *        The options OBJECT, already parsed.
	 */
	private void processProxy(final Options options, final Map<String, MetaExpression> optionsParameters) {
		MetaExpression proxyPortME = optionsParameters.get("proxyport");
		if (proxyPortME == null) {
			throw new RobotRuntimeException("Proxyport must be given in the options OBJECT");
		}
		options.setProxyPort(proxyPortME.getNumberValue().intValue());

		String proxyUser = getString(optionsParameters, "proxyuser");
		String proxyPass = getString(optionsParameters, "proxypass");
		String proxyType = getString(optionsParameters, "proxytype");

		// Check if the proxyUserValue is null or empty
		// Check if the proxyPassValue is null or empty
		boolean proxyUserEmpty = proxyUser == null || proxyUser.isEmpty();
		boolean proxyPassEmpty = proxyPass == null || proxyPass.isEmpty();
		if (proxyUserEmpty != proxyPassEmpty) {
			throw new RobotRuntimeException("The Proxyuser and proxypass must either both be set up in the options OBJECT or none of them.");
		}

		if (proxyType == null) {
			proxyType = "http";
		}
		if (!"http".equalsIgnoreCase(proxyType) && !"socks5".equalsIgnoreCase(proxyType)) {
			throw new RobotRuntimeException("Invalid proxy type.");
		}

		options.setProxyUser(proxyUser);
		options.setProxyPass(proxyPass);
		options.setProxyType(proxyType);
	}

	private void processOption(final Options options, final Map<String, MetaExpression> optionParameters, final String option, final MetaExpression value) {

		switch (option) {

			case "proxyhost":
				options.setProxyHost(value.getStringValue());
				processProxy(options, optionParameters);
				break;

				// needed if we want to set proxyhost
			case "proxyport":
			case "proxyuser":
			case "proxypass":
			case "proxytype":
				break;

			case "enablejs":
				options.enableJS(value.getBooleanValue());
				break;

			case "enablewebsecurity":
				options.enableWebSecurity(value.getBooleanValue());
				break;

			case "loadimages":
				options.enableLoadImages(value.getBooleanValue());
				break;

			case "insecuressl":
				options.enableInsecureSSL(value.getBooleanValue());
				break;

			case "timeout":
				options.setTimeout(value.getNumberValue().intValue());
				break;

			case "ltrurlaccess":
				options.enableLtrUrlAccess(value.getBooleanValue());
				break;

			case "sslprotocol":
				String sslProtocol = value.getStringValue();
				if (!sslProtocol.equalsIgnoreCase("sslv3") && !sslProtocol.equalsIgnoreCase("sslv2") && !sslProtocol.equalsIgnoreCase("tlsv1") && !sslProtocol.equalsIgnoreCase("any")) {
					throw new RobotRuntimeException("Invalid sslprotocol.");
				}
				else {
					options.setSslProtocol(sslProtocol);
				}
				break;

			case "user":
				options.setHttpAuthUser(value.getStringValue());
				String httpAuthPass = getString(optionParameters, "pass");
				if (httpAuthPass == null || httpAuthPass.isEmpty()) {
					throw new RobotRuntimeException("Http password must be set if user is used.");
				}
				else {
					options.setHttpAuthPass(httpAuthPass);
				}
				break;

				// Needed if we want to set user
			case "pass":
				break;

			case "browser":
				String browser = value.getStringValue();
				if (!"PHANTOMJS".equals(browser)) {
					throw new RobotRuntimeException("Invalid \"browser\" option.");
				}
				else {
					options.setBrowser(browser);
				}
				break;

			default:
				throw new RobotRuntimeException("Unknow option: " + option);
		}
	}

	/**
	 * Process the options handed as a metaExpression.
	 * 
	 * @param optionsVar
	 *        The options we want to set. If it is null we return a default value.
	 * @return
	 *         A new {@link Options}.
	 */
	public Options processOptions(final MetaExpression optionsVar) {
		Options options = new Options();
		// no option specified - so default is used.
		if (optionsVar == ExpressionBuilderHelper.NULL || optionsVar.isNull()) {
			return options;
		}
		else {

			if (optionsVar.getType() != ExpressionDataType.OBJECT) {
				throw new RobotRuntimeException("Invalid options variable!");
			}
			@SuppressWarnings("unchecked")
			Map<String, MetaExpression> optionParameters = (Map<String, MetaExpression>) optionsVar.getValue();

			for (Map.Entry<String, MetaExpression> entry : optionParameters.entrySet()) {
				processOption(options, optionParameters, entry.getKey(), entry.getValue());
			}
		}

		return options;
	}

	/**
	 * Retrieves a string value from an option in options.
	 * Note that this function must not be called if the option is not in the options
	 * 
	 * @param options
	 *        A map containing all the options.
	 * @param option
	 *        The option we want.
	 * @return
	 *         The stringvalue of the requested option.
	 */
	private static String getString(final Map<String, MetaExpression> options, final String option) {
		MetaExpression me = options.get(option);
		return me.getStringValue();
	}

}
