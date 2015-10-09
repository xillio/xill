package nl.xillio.xill.plugins.rest.data;

import java.util.Map;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import org.apache.http.HttpHost;
import org.apache.http.client.fluent.Executor;

/**
 * Support class for processing request options
 */
public class Options {

	private int timeout = 5000;

	private String proxyHost = "";
	private int proxyPort = 0;
	private String proxyUser = "";
	private String proxyPass = "";

	/**
	 * @param optionsVar the map of options and their values for request operation
	 */
	public Options(final MetaExpression optionsVar) {

		if (optionsVar == ExpressionBuilderHelper.NULL || optionsVar.isNull()) {
			// no option specified - so default is used
			return;
		}

		if (optionsVar.getType() != ExpressionDataType.OBJECT) {
			throw new RobotRuntimeException("Invalid options variable!");
		}

		@SuppressWarnings("unchecked")
		Map<String, MetaExpression> optionParameters = (Map<String, MetaExpression>) optionsVar.getValue();
		for (Map.Entry<String, MetaExpression> entry : optionParameters.entrySet()) {
			processOption(optionParameters, entry.getKey(), entry.getValue());
		}

		this.checkProxyOptions();
	}

	private void processOption(final Map<String, MetaExpression> optionParameters, final String option, final MetaExpression value) {
		switch (option) {

			case "timeout":
				this.timeout = value.getNumberValue().intValue();
				break;

			case "proxyhost":
				this.proxyHost = value.getStringValue();
				break;

			case "proxyport":
				this.proxyPort = value.getNumberValue().intValue();
				break;

			case "proxyuser":
				this.proxyUser = value.getStringValue();
				break;

			case "proxypass":
				this.proxyPass = value.getStringValue();
				break;

			default:
				throw new RobotRuntimeException(String.format("Option [%1$s] is invalid!", option));
		}
	}

	private void checkProxyOptions() {
		if (this.proxyUser.isEmpty() != this.proxyPass.isEmpty()) {
			throw new RobotRuntimeException("User and password must be set both!");
		}

		if ((this.proxyPort != 0) && (this.proxyHost.isEmpty())) {
			throw new RobotRuntimeException("Port cannot be set without host!");
		}
	}

	/**
	 * @return the request processing timeout
	 */
	public int getTimeout() {
		return this.timeout;
	}

	/**
	 * Set a authentication method to executor
	 * 
	 * @param executor request executor
	 */
	public void doAuth(Executor executor) {
		HttpHost host = null;
		if (!this.proxyHost.isEmpty()) {
			if (this.proxyPort == 0) {
				host = new HttpHost(this.proxyHost);
			} else {
				host = new HttpHost(this.proxyHost, this.proxyPort);
			}
		}

		if (this.proxyUser.isEmpty()) {
			if (host != null) {
				executor.authPreemptive(host);
			}
		} else {
			if (host != null) {
				executor.auth(host, this.proxyUser, this.proxyPass);
			} else {
				executor.auth(this.proxyUser, this.proxyPass);
			}
		}
	}

}
