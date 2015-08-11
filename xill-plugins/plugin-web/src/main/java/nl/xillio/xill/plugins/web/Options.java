package nl.xillio.xill.plugins.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import nl.xillio.xill.api.components.MetadataExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import org.apache.commons.codec.binary.Base64;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * Class encapsulate PhantomJS options handling (parsing, validating, creating new PhantomJS process, etc.)
 * Class attributes represents all browser options for use in the loadpage function
 * It contains both CLI options and non-CLI options.
 * CLI options are those that must be provided when PhantomJS is starting and cannot be changed anymore for already started PhantomJS process
 * non-CLI options are those that can be set whenever at whatever existing PhantomJS process
 */
public class Options implements MetadataExpression {

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

	/**
	 * Set the proxy host.
	 * @param name
	 * 					The name of the host.
	 */
	public void setProxyHost(final String name) {
		proxyHost = name;
	}

	/**
	 * Set the proxy port.
	 * @param value
	 * 					The name of the port.
	 */
	public void setProxyPort(final int value) {
		proxyPort = value;
	}

	/**
	 * Set the timeout value.
	 * @param value
	 * 					The value of the timeout in ms.
	 */
	public void setTimeout(final int value) {
		timeout = value;
	}

	/**
	 * Set the proxy user.
	 * @param name
	 * 					The username.
	 */
	public void setProxyUser(final String name) {
		proxyUser = name;
	}

	/**
	 * Set the proxy pass.
	 * @param name
	 * 				 The name of the pass.
	 */
	public void setProxyPass(final String name) {
		proxyPass = name;
	}

	/**
	 * Set proxy type.
	 * @param name
	 * 					The name of the proxyType. (Supported: http, socks5)
	 */
	public void setProxyType(final String name) {
		proxyType = name;
	}

	/**
	 * Set the httpAuthUser.
	 * @param name
	 * 					The name of the user.
	 */
	public void setHttpAuthUser(final String name) {
		httpAuthUser = name;
	}

	/**
	 * Set a pass for the httpAuthUser.
	 * @param name
	 * 					The name of the pass.
	 */
	public void setHttpAuthPass(final String name) {
		httpAuthPass = name;
	}

	/**
	 * Set the browser.
	 * @param name
	 * 					The name of the browser. (currently supported: PHANTOMJS)
	 */
	public void setBrowser(final String name) {
		browser = name;
	}

	/**
	 * Set the sslProtocol.
	 * @param name
	 * 					The name of the protocol. (supported: sslv3, sslv2, tlsv1, any).
	
	 */
	public void setSslProtocol(final String name) {
		sslProtocol = name;
	}

	/**
	 * Enable or disable JavaScript.
	 * @param enabled
	 * 					Whether or not we want JS enabled.
	 */
	public void enableJS(final boolean enabled) {
		enableJS = enabled;
	}

	/**
	 * Enable of disable WebSecurity.
	 * @param enabled
	 * 					Whether or not we want security enabled.
	 */
	public void enableWebSecurity(final boolean enabled) {
		enableWebSecurity = enabled;
	}

	/**
	 * Enable or disable insecure SSL.
	 * @param enabled
	 * 					Whether or not we want insecure SSL enabled.
	 */
	public void enableInsecureSSL(final boolean enabled) {
		insecureSSL = enabled;
	}

	/**
	 * Enable or disable load images.
	 * @param enabled
	 * 					Whether or not we want load images enabled.
	 */
	public void enableLoadImages(final boolean enabled) {
		loadImages = enabled;
	}

	/**
	 * Enable or disable ltr URL access.
	 * @param enabled
	 * 					Whether or not we want this access enabled.
	 */
	public void enableLtrUrlAccess(final boolean enabled) {
		ltrUrlAccess = enabled;
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

	/**
	 * Method deletes all existing PhantomJS process files from temp folder (on
	 * Windows only) There are cases when the file is not removed after CT is
	 * closed (e.g. when CT crashes or is manually terminated, etc.) This
	 * prevents from cumulating useless files in the system.
	 */
	public static void cleanUnusedPJSExe() {
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
			throw new RobotRuntimeException("IO error when deleting existing PhantomJS files  from temp folder", e);
		}
	}

	/**
	 * Creates new PhantomJS.exe file in temporary folder - on MS Windows only
	 * For other operating systems, PhantomJS is expected to be properly installed in the path.
	 */
	public static void extractNativeBinary() {

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

	/**
	 * @return
	 *         Returns the timeOut value in the options.
	 */
	public int getTimeOut() {
		return timeout;
	}

}
