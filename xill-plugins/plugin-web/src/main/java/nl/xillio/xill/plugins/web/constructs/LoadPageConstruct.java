package nl.xillio.xill.plugins.web.constructs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.PageVariable;
import nl.xillio.xill.plugins.web.PhantomJSPool;
import nl.xillio.xill.plugins.web.WebPluginPackage;

public class LoadPageConstruct extends Construct implements AutoCloseable {

	private static final PhantomJSPool pool = new PhantomJSPool(10);

	static {
		cleanUnusedPJSExe();
		extractNativeBinary();
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(LoadPageConstruct::process, new Argument("url"), new Argument("options", NULL));
	}

	public static MetaExpression process(final MetaExpression urlVar, final MetaExpression optionsVar) {

		String url = urlVar.getStringValue();

		Options options = new Options();
		try {
			// processing input options
			options.processOptions(optionsVar);
		} catch (Exception e) {
			throw new RobotRuntimeException("Failed to convert LoadPage options '" + optionsVar.getValue(), e);
		}

		// getting properly configured webdriver
		PhantomJSPool.Entity item = LoadPageConstruct.pool.get(LoadPageConstruct.pool.createIdentifier(options));
		if (item == null) {
			throw new RobotRuntimeException("Loadpage error - PhantomJS pool is fully used and cannot provide another instance!");
		}
		PhantomJSDriver driver = (PhantomJSDriver) item.getDriver();

		try {
			// get the page
			driver.get(url);
		} catch (TimeoutException e) {
			throw new RobotRuntimeException("Loadpage timeout", e);
		}

		return PageVariable.create(item);
	}

	/**
	 * @author Zbynek Hochmann Set of browser options for use with the loadpage
	 *         function
	 */
	public static class Options {

		// Driver options
		private int timeout = 0;

		// DCap options
		private String browser;
		private boolean enableJS = true;
		private boolean enableWebSecurity = true;
		private boolean insecureSSL = false;
		private boolean loadImages = true;
		private String sslProtocol; // null==(default==) "sslv3"
		private boolean ltrUrlAccess; // --local-to-remote-url-access
		// (default==null== "false")

		private String proxyHost;
		private int proxyPort = 0;
		private String proxyUser;
		private String proxyPass;
		private String proxyType;
		private String httpAuthUser;
		private String httpAuthPass;

		private void processProxy(final Map<String, MetaExpression> options) throws Exception {
			MetaExpression proxyPortME = options.get("proxyport");
			if (proxyPortME == null) {
				throw new Exception("Proxyport must be a valid number.");
			}
			proxyPort = proxyPortME.getNumberValue().intValue();

			proxyUser = getString(options, "proxyuser");
			proxyPass = getString(options, "proxypass");
			proxyType = getString(options, "proxytype");

			boolean proxyUserEmpty = proxyUser == null ? true : proxyUser.isEmpty();
			boolean proxyPassEmpty = proxyPass == null ? true : proxyPass.isEmpty();
			if (proxyUserEmpty != proxyPassEmpty) {
				throw new Exception("Proxyuser and proxypass must be set up or none of them.");
			}

			if (proxyType == null) {
				proxyType = "http";
			}
			if (!proxyType.equalsIgnoreCase("http") && !proxyType.equalsIgnoreCase("socks5")) {
				throw new Exception("Invalid proxytype.");
			}
		}

		private void processOption(final Map<String, MetaExpression> options, final String option, final MetaExpression value) throws Exception {

			switch (option) {

				case "proxyhost":
					proxyHost = value.getStringValue();
					processProxy(options);
					break;

				case "enablejs":
					enableJS = value.getBooleanValue();
					break;

				case "enablewebsecurity":
					enableWebSecurity = value.getBooleanValue();
					break;

				case "loadimages":
					loadImages = value.getBooleanValue();
					break;

				case "insecuressl":
					insecureSSL = value.getBooleanValue();
					break;

				case "timeout":
					timeout = value.getNumberValue().intValue();
					break;

				case "ltrurlaccess":
					ltrUrlAccess = value.getBooleanValue();
					break;

				case "sslprotocol":
					sslProtocol = value.getStringValue();
					if (!sslProtocol.equalsIgnoreCase("sslv3") && !sslProtocol.equalsIgnoreCase("sslv2") && !sslProtocol.equalsIgnoreCase("tlsv1") && !sslProtocol.equalsIgnoreCase("any")) {
						throw new Exception("Invalid sslprotocol.");
					}
					break;

				case "user":
					httpAuthUser = value.getStringValue();
					httpAuthPass = getString(options, "pass");
					if (httpAuthPass == null || httpAuthPass.isEmpty()) {
						throw new Exception("Http password must be set if user is used.");
					}
					break;

				case "browser":
					browser = value.getStringValue();
					if (!browser.equals("PHANTOMJS")) {
						throw new Exception("Invalid \"browser\" option.");
					}
					break;

				default:
					throw new Exception("Unknow option: " + option);
			}
		}

		private void processOptions(final MetaExpression optionsVar) throws Exception {
			if (optionsVar.isNull()) { // no option specified - so default is
				// used
				return;
			}
			// else

			if (optionsVar.getType() != ExpressionDataType.OBJECT) {
				throw new Exception("Invalid options variable!");
			}
			@SuppressWarnings("unchecked")
			Map<String, MetaExpression> options = (Map<String, MetaExpression>) optionsVar.getValue();

			for (Map.Entry<String, MetaExpression> entry : options.entrySet()) {
				processOption(options, entry.getKey(), entry.getValue());
			}
		}

		private static String getString(final Map<String, MetaExpression> options, final String option) {
			MetaExpression me = options.get(option);
			if (me != null) {
				return me.getStringValue();
			} else {
				return null;
			}
		}

		public String getProxyUser() {
			return proxyUser;
		}

		public String getProxyPass() {
			return proxyPass;
		}

		public String getHttpAuthUser() {
			return httpAuthUser;
		}

		public String getHttpAuthPass() {
			return httpAuthPass;
		}

		public WebDriver createDriver() {
			return createPhantomJSDriver();
		}

		/**
		 * It sets the options that are to be set after the driver is created
		 *
		 * @param driver
		 *        Existing WebDriver
		 */
		public void setDriverOptions(final WebDriver driver) {
			driver.manage().window().setSize(new Dimension(1920, 1080)); // setting
			// up
			// bigger
			// size
			// of
			// viewport
			// (default
			// is
			// 400x300)

			// page load timeout
			if (timeout != 0) {
				driver.manage().timeouts().pageLoadTimeout(timeout, TimeUnit.MILLISECONDS);
			} else {
				// set infinite timeout
				driver.manage().timeouts().pageLoadTimeout(-1, TimeUnit.MILLISECONDS);
			}

			// driver.manage().deleteAllCookies();
		}

		/**
		 * Creates CLI options
		 *
		 * @return The object encapsulating all CLI parameters for PhantomJS.exe
		 *         process
		 */
		private DesiredCapabilities createDCapOptions() {
			// DesiredCapabilities dcap = new DesiredCapabilities();
			DesiredCapabilities dcap = DesiredCapabilities.phantomjs();

			// enable JavaScript
			dcap.setJavascriptEnabled(enableJS);

			ArrayList<String> phantomArgs = new ArrayList<String>();
			// phantomArgs.add("--webdriver-logfile=NONE"); //! this option
			// doesn't work (why not?) - it will create an empty file anyway
			phantomArgs.add("--webdriver-loglevel=NONE");// values can be NONE |
			// ERROR | WARN | INFO
			// | DEBUG (if NONE
			// then the log file is
			// not created)
			phantomArgs.add("--ignore-ssl-errors=" + (insecureSSL ? "true" : "false"));
			phantomArgs.add("--load-images=" + (loadImages ? "true" : "false"));
			phantomArgs.add("--web-security=" + (enableWebSecurity ? "true" : "false"));
			phantomArgs.add("--local-to-remote-url-access=" + (ltrUrlAccess ? "true" : "false"));

			if (sslProtocol != null) {
				phantomArgs.add("--ssl-protocol=" + sslProtocol);
			}

			if (proxyHost != null) {
				phantomArgs.add("--proxy-type=" + proxyType);
				phantomArgs.add(String.format("--proxy=%1$s:%2$d", proxyHost, proxyPort));
				if (proxyUser != null) {
					phantomArgs.add(String.format("--proxy-auth=%1$s:%2$s", proxyUser, proxyPass));
				}
			}

			if (httpAuthUser != null) {
				String s = String.format("%1$s:%2$s", httpAuthUser, httpAuthPass);
				dcap.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_CUSTOMHEADERS_PREFIX, "Authorization: Basic " + Base64.encodeBase64String(s.getBytes()));
			}

			dcap.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, phantomArgs);

			return dcap;
		}

		private static boolean strEq(final String s1, final String s2) {
			if (s1 == null) {
				return s2 == null;
			}
			if (s2 == null) {
				return false;
			}
			return s1.equals(s2);
		}

		/**
		 * It compares provided options with this options
		 *
		 * @param options
		 *        contains actual LoadPage settings
		 * @return true if both options exactly matches otherwise false
		 */
		public boolean compareDCap(final Options options) {
			return strEq(browser, options.browser) && enableJS == options.enableJS && enableWebSecurity == options.enableWebSecurity && insecureSSL == options.insecureSSL
							&& loadImages == options.loadImages && strEq(sslProtocol, options.sslProtocol) && ltrUrlAccess == options.ltrUrlAccess && strEq(proxyHost, options.proxyHost)
							&& proxyPort == options.proxyPort && strEq(proxyUser, options.proxyUser) && strEq(proxyPass, options.proxyPass) && strEq(proxyType, options.proxyType)
							&& strEq(httpAuthUser, options.httpAuthUser) && strEq(httpAuthPass, options.httpAuthPass);
		}

		private WebDriver createPhantomJSDriver() {
			DesiredCapabilities dcap = createDCapOptions();// creates CLI
			// options
			PhantomJSDriver driver = new PhantomJSDriver(dcap);// creates new
			// PhantomJS..exe
			// process with
			// given CLI
			// options
			setDriverOptions(driver); // set other (non-CLI) options
			return driver;
		}
	}// end of class Options

	/*
	 * Method deletes all existing phantomjs..exe files from temp folder (on
	 * Windows only) There are cases when the file is not removed after CT is
	 * closed (e.g. when CT crashes or is manually terminated, etc.) This
	 * prevents from cumulating useless files in the system.
	 */
	private static void cleanUnusedPJSExe() {
		try {
			File phantomJStoolBinary;

			String os = System.getProperty("os.name").toLowerCase();
			// Windows
			if (os.indexOf("win") >= 0) {
				phantomJStoolBinary = File.createTempFile("phantomjs", ".exe");
				String path = phantomJStoolBinary.toPath().getParent().toString();
				phantomJStoolBinary.delete();

				// delete all phantomjs...exe files
				File dir = new File(path);
				File[] files = dir.listFiles((final File file, final String name) -> name.startsWith("phantomjs") && name.endsWith(".exe"));
				for (File file : files) {
					try {
						file.delete();
					} catch (Exception e) {}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void extractNativeBinary() {

		try {
			File phantomJStoolBinary;
			String nativeBinarySource;

			String os = System.getProperty("os.name").toLowerCase();
			// Windows
			if (os.indexOf("win") >= 0) {
				phantomJStoolBinary = File.createTempFile("phantomjs", ".exe");
				nativeBinarySource = "/phantomjs/phantomjs.exe";

				phantomJStoolBinary.deleteOnExit();
				String phantomJStoolPath = phantomJStoolBinary.getAbsolutePath();

				System.setProperty("phantomjs.binary.path", phantomJStoolPath);

				// extract file into the current directory
				InputStream reader = WebPluginPackage.class.getResourceAsStream(nativeBinarySource);
				if (reader == null) {
					throw new Exception("Cannot find phantomjs.exe resource file!");
				}
				FileOutputStream writer = new FileOutputStream(phantomJStoolPath);
				byte[] buffer = new byte[1024];
				int bytesRead = 0;
				while ((bytesRead = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, bytesRead);
				}

				writer.close();
				reader.close();
				return;
			}

			// For other OS's, phantomJS is expected to be installed in the path
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return;
		}
	}

	@Override
	public void close() throws Exception {
		pool.dispose();
	}

}
