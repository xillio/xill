package nl.xillio.xill.plugins.web.constructs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.PageVariable;
import nl.xillio.xill.plugins.web.PhantomJSConstruct;
import nl.xillio.xill.plugins.web.PhantomJSPool;
import nl.xillio.xill.plugins.web.WebXillPlugin;
import nl.xillio.xill.plugins.web.services.web.WebService;

import org.apache.commons.codec.binary.Base64;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * @author Zbynek Hochmann
 *         Loads the new page via PhantomJS process and holds the context of a page
 */
public class LoadPageConstruct extends PhantomJSConstruct implements AutoCloseable {

	private static final PhantomJSPool pool = new PhantomJSPool(10);

	static {
		cleanUnusedPJSExe();
		extractNativeBinary();
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(url, options) -> process(url, options, webService),
			new Argument("url"),
			new Argument("options", NULL));
	}

	/**
	 * @param urlVar
	 *        string variable - page URL
	 * @param optionsVar
	 *        list variable - options for loading the page (see CT help for details)
	 * @return PAGE variable
	 */
	public static MetaExpression process(final MetaExpression urlVar, final MetaExpression optionsVar, final WebService webService) {

		String url = urlVar.getStringValue();

		Options options = new Options();
		try {
			// processing input options
			options.processOptions(optionsVar);
		} catch (Exception e) {
			throw new RobotRuntimeException("Failed to convert LoadPage options '" + optionsVar.getValue(), e);
		}

		// getting properly configured webdriver
		PageVariable item = LoadPageConstruct.pool.get(LoadPageConstruct.pool.createIdentifier(options)).getPage();
		if (item == null) {
			throw new RobotRuntimeException("Loadpage error - PhantomJS pool is fully used and cannot provide another instance!");
		}
		PhantomJSDriver driver = (PhantomJSDriver) item.getDriver();

		try {
			URL newUrl;
			try {
				newUrl = new URL(url);

				if (newUrl.getRef() != null) {
					driver.get("about:blank"); // this is because of (something like) clearing the PJS cache (CTC-667)
				}

			} catch (MalformedURLException e) {
				e.printStackTrace();
			}

			driver.get(url);
		} catch (TimeoutException e) {
			throw new RobotRuntimeException("Loadpage timeout", e);
		}

		return createPage(item, webService);
	}

	/**
	 * Class encapsulate PhantomJS options handling (parsing, validating, creating new PhantomJS process, etc.)
	 * Class attributes represents all browser options for use in the loadpage function
	 * It contains both CLI options and non-CLI options.
	 * CLI options are those that must be provided when PhantomJS is starting and cannot be changed anymore for already started PhantomJS process
	 * non-CLI options are those that can be set whenever at whatever existing PhantomJS process
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
		private boolean ltrUrlAccess; // --local-to-remote-url-access (default==null== "false")

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
						throw new RobotRuntimeException("Invalid sslprotocol.");
					}
					break;

				case "user":
					httpAuthUser = value.getStringValue();
					httpAuthPass = getString(options, "pass");
					if (httpAuthPass == null || httpAuthPass.isEmpty()) {
						throw new RobotRuntimeException("Http password must be set if user is used.");
					}
					break;

				case "browser":
					browser = value.getStringValue();
					if (!browser.equals("PHANTOMJS")) {
						throw new RobotRuntimeException("Invalid \"browser\" option.");
					}
					break;

				default:
					throw new RobotRuntimeException("Unknow option: " + option);
			}
		}

		private void processOptions(final MetaExpression optionsVar) throws Exception {
			//no option specified - so default is used.
			if (optionsVar.isNull()) {
				return;
			}
			else{

			if (optionsVar.getType() != ExpressionDataType.OBJECT) {
				throw new Exception("Invalid options variable!");
			}
			@SuppressWarnings("unchecked")
			Map<String, MetaExpression> options = (Map<String, MetaExpression>) optionsVar.getValue();

			for (Map.Entry<String, MetaExpression> entry : options.entrySet()) {
				processOption(options, entry.getKey(), entry.getValue());
			}
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

		/**
		 * @return current proxy user
		 */
		public String getProxyUser() {
			return proxyUser;
		}

		/**
		 * @return current proxy pasword
		 */
		public String getProxyPass() {
			return proxyPass;
		}

		/**
		 * @return current HTTP auth user
		 */
		public String getHttpAuthUser() {
			return httpAuthUser;
		}

		/**
		 * @return current HTTP auth password
		 */
		public String getHttpAuthPass() {
			return httpAuthPass;
		}

		/**
		 * Creates new PhantomJS process - it uses current (CLI + non-CLI) options for starting the process
		 *
		 * @return newly created PhantomJS process
		 */
		public WebDriver createDriver() {
			return createPhantomJSDriver();
		}

		/**
		 * It sets the non-CLI options (i.e. the option that can be set after the process is created)
		 *
		 * @param driver
		 *        Existing WebDriver
		 */
		public void setDriverOptions(final WebDriver driver) {
			// setting up bigger size of viewport (default is 400x300)
			driver.manage().window().setSize(new Dimension(1920, 1080));

			// page load timeout
			if (timeout != 0) {
				driver.manage().timeouts().pageLoadTimeout(timeout, TimeUnit.MILLISECONDS);
			} else {
				// set infinite timeout
				driver.manage().timeouts().pageLoadTimeout(-1, TimeUnit.MILLISECONDS);
			}

			// driver.manage().deleteAllCookies(); - probably not needed
		}

		/**
		 * Creates the object that holds all current CLI options.
		 *
		 * @return The object encapsulating all CLI parameters for PhantomJS process
		 */
		private DesiredCapabilities createDCapOptions() {
			DesiredCapabilities dcap = DesiredCapabilities.phantomjs();

			// enable JavaScript
			dcap.setJavascriptEnabled(enableJS);

			ArrayList<String> phantomArgs = new ArrayList<String>();
			phantomArgs.add("--disk-cache=false");
			// phantomArgs.add("--webdriver-logfile=NONE"); //! this option doesn't work (why not?) - it will create an empty file anyway
			phantomArgs.add("--webdriver-loglevel=NONE");// values can be NONE | ERROR | WARN | INFO | DEBUG (if NONE then the log file is not created)
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

		/**
		 * @param s1
		 *        first string value
		 * @param s2
		 *        second string value
		 * @return if provided string are equal or not (including null strings)
		 */
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
		 * It compares provided CLI options with current CLI options
		 *
		 * @param options
		 *        contains actual LoadPage CLI settings
		 * @return true if matches otherwise false
		 */
		public boolean compareDCap(final Options options) {
			return strEq(browser, options.browser) && enableJS == options.enableJS && enableWebSecurity == options.enableWebSecurity && insecureSSL == options.insecureSSL
					&& loadImages == options.loadImages && strEq(sslProtocol, options.sslProtocol) && ltrUrlAccess == options.ltrUrlAccess && strEq(proxyHost, options.proxyHost)
					&& proxyPort == options.proxyPort && strEq(proxyUser, options.proxyUser) && strEq(proxyPass, options.proxyPass) && strEq(proxyType, options.proxyType)
					&& strEq(httpAuthUser, options.httpAuthUser) && strEq(httpAuthPass, options.httpAuthPass);
		}

		private WebDriver createPhantomJSDriver() {
			// creates CLI options
			DesiredCapabilities dcap = createDCapOptions();

			// creates new PhantomJS process with given CLI options
			PhantomJSDriver driver = new PhantomJSDriver(dcap);

			// set other (non-CLI) options
			setDriverOptions(driver);

			return driver;
		}
	}// end of class Options

	/*
	 * Method deletes all existing PhantomJS process files from temp folder (on
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

				// delete all phantomjs process files
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

	/*
	 * Creates new PhantomJS.exe file in temporary folder - on MS Windows only
	 * For other operating systems, PhantomJS is expected to be properly installed in the path.
	 */
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
				InputStream reader = WebXillPlugin.class.getResourceAsStream(nativeBinarySource);
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
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return;
		}
	}

	@Override
	public void close() throws Exception {
		// it will dispose entire PJS pool (all PJS processes will be terminated and temporary PJS files deleted)
		pool.dispose();
	}

}
