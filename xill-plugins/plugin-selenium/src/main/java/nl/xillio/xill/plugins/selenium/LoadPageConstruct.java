package nl.xillio.xill.plugins.selenium;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import nl.xillio.xill.api.components.AtomicExpression;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import org.apache.commons.codec.binary.Base64;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;

import java.util.concurrent.TimeUnit;

/**
 *
 */
public class LoadPageConstruct implements Construct, AutoCloseable {

	private static final PhantomJSPool pool = new PhantomJSPool(10);

	static {
		extractNativeBinary();
	}
	
	@Override
	public String getName() {
		return "loadpage";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			LoadPageConstruct::process,
			new Argument("url"),
			new Argument("options", new AtomicExpression("test")));
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
/*!!		
		Consumer<RobotID> listener = new Consumer<RobotID>() {
			@Override
			public void accept(RobotID t) {
				onRobotStop(item);
			}}; 
			this.robot.getOnRobotStopped().addDisposableListener(listener);
*/
		try {
			//get the page
			driver.get(url);
		} catch (TimeoutException e) {
			throw new RobotRuntimeException("Loadpage timeout", e);
		}

		return PageVariable.create(item);
	}

	/**
	 * @author Zbynek Hochmann
	 *         Set of browser options for use with the loadpage function
	 */
	public static class Options {

		//Driver options
		private int timeout = 0;

		//DCap options
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

			this.proxyUser = getString(options, "proxyuser");
			this.proxyPass = getString(options, "proxypass");
			this.proxyType = getString(options, "proxytype");

			boolean proxyUserEmpty = (this.proxyUser == null) ? true : this.proxyUser.isEmpty();
			boolean proxyPassEmpty = (this.proxyPass == null) ? true : this.proxyPass.isEmpty();
			if (proxyUserEmpty != proxyPassEmpty) {
				throw new Exception("Proxyuser and proxypass must be set up or none of them.");
			}

			if (this.proxyType == null) {
				this.proxyType = "http";
			}
			if ((!this.proxyType.equalsIgnoreCase("http")) && (!this.proxyType.equalsIgnoreCase("socks5"))) {
				throw new Exception("Invalid proxytype.");
			}		
		}
		
		private void processOption(final Map<String, MetaExpression> options, final String option, final MetaExpression value) throws Exception {
		
			switch (option) {

				case "proxyhost":
					this.proxyHost = value.getStringValue();
					this.processProxy(options);
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
					this.timeout = value.getNumberValue().intValue();
					break;
					
				case "ltrurlaccess":
					this.ltrUrlAccess = value.getBooleanValue();
					break;
					
				case "sslprotocol":
					this.sslProtocol = value.getStringValue();
					if ((!this.sslProtocol.equalsIgnoreCase("sslv3")) && (!this.sslProtocol.equalsIgnoreCase("sslv2"))
							&& (!this.sslProtocol.equalsIgnoreCase("tlsv1")) && (!this.sslProtocol.equalsIgnoreCase("any"))) {
						throw new Exception("Invalid sslprotocol.");
					}
					break;
					
				case "user":
					this.httpAuthUser = value.getStringValue();
					this.httpAuthPass = getString(options, "pass");
					if ((this.httpAuthPass == null) || (this.httpAuthPass.isEmpty())) {
						throw new Exception("Http password must be set if user is used.");
					}
					break;
					
				case "browser":
					this.browser = value.getStringValue();
					if (!this.browser.equals("PHANTOMJS")) {
						throw new Exception("Invalid \"browser\" option.");
					}
					break;
					
				default:
					throw new Exception("Unknow option: " + option);
			}
		}

		private void processOptions(final MetaExpression optionsVar) throws Exception {
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
			return this.proxyUser;
		}

		public String getProxyPass() {
			return this.proxyPass;
		}

		public String getHttpAuthUser() {
			return this.httpAuthUser;
		}

		public String getHttpAuthPass() {
			return this.httpAuthPass;
		}

		public WebDriver createDriver() {
			return this.createPhantomJSDriver();
		}

		/**
		 * It sets the options that are to be set after the driver is created
		 * @param driver Existing WebDriver
		 */
		public void setDriverOptions(final WebDriver driver) {
			driver.manage().window().setSize(new Dimension(1920, 1080)); //setting up bigger size of viewport (default is 400x300)

			// page load timeout
			if (this.timeout != 0) {
				driver.manage().timeouts().pageLoadTimeout(this.timeout, TimeUnit.MILLISECONDS);
			} else {
				//set infinite timeout
				driver.manage().timeouts().pageLoadTimeout(-1, TimeUnit.MILLISECONDS);
			}
			
			//driver.manage().deleteAllCookies();
		}
		
		/**
		 * Creates CLI options 
		 * @return The object encapsulating all CLI parameters for PhantomJS.exe process
		 */
		private DesiredCapabilities createDCapOptions() {
			// DesiredCapabilities dcap = new DesiredCapabilities();
			DesiredCapabilities dcap = DesiredCapabilities.phantomjs();

			//enable JavaScript
			dcap.setJavascriptEnabled(this.enableJS);

			ArrayList<String> phantomArgs = new ArrayList<String>();
			// phantomArgs.add("--webdriver-logfile=NONE"); //! this option doesn't work (why not?) - it will create an empty file anyway
			phantomArgs.add("--webdriver-loglevel=NONE");// values can be NONE | ERROR | WARN | INFO | DEBUG (if NONE then the log file is not created)
			phantomArgs.add("--ignore-ssl-errors=" + (this.insecureSSL ? "true" : "false"));
			phantomArgs.add("--load-images=" + (this.loadImages ? "true" : "false"));
			phantomArgs.add("--web-security=" + (this.enableWebSecurity ? "true" : "false"));
			phantomArgs.add("--local-to-remote-url-access=" + (this.ltrUrlAccess ? "true" : "false"));

			if (this.sslProtocol != null) {
				phantomArgs.add("--ssl-protocol=" + this.sslProtocol);
			}

			if (this.proxyHost != null) {
				phantomArgs.add("--proxy-type=" + this.proxyType);
				phantomArgs.add(String.format("--proxy=%1$s:%2$d", this.proxyHost, this.proxyPort));
				if (this.proxyUser != null) {
					phantomArgs.add(String.format("--proxy-auth=%1$s:%2$s", this.proxyUser, this.proxyPass));
				}
			}

			if (this.httpAuthUser != null) {
				String s = String.format("%1$s:%2$s", this.httpAuthUser, this.httpAuthPass);
				dcap.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_CUSTOMHEADERS_PREFIX, "Authorization: Basic " + Base64.encodeBase64String(s.getBytes()));
			}

			dcap.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, phantomArgs);

			return dcap;
		}
		
		private boolean strEq(final String s1, final String s2) {
			if (s1 == null) {
				return (s2 == null);
			} else {
				if (s2 == null) {
					return false;
				}
			}
			return s1.equals(s2);
		}
		
		/**
		 * It compares provided options with this options
		 * @param options contains actual LoadPage settings
		 * @return true if both options exactly matches otherwise false
		 */
		public boolean compareDCap(final Options options) {
			return ( (strEq(this.browser, options.browser)) && (this.enableJS == options.enableJS) && (this.enableWebSecurity == options.enableWebSecurity) &&
					(this.insecureSSL == options.insecureSSL) && (this.loadImages == options.loadImages) && (strEq(this.sslProtocol,options.sslProtocol)) &&
					(this.ltrUrlAccess == options.ltrUrlAccess) && (strEq(this.proxyHost,options.proxyHost)) && (this.proxyPort == options.proxyPort) && 
					(strEq(this.proxyUser,options.proxyUser)) && (strEq(this.proxyPass,options.proxyPass)) && (strEq(this.proxyType,options.proxyType)) && 
					(strEq(this.httpAuthUser,options.httpAuthUser)) && (strEq(this.httpAuthPass,options.httpAuthPass)) ); 
		}
		
		private WebDriver createPhantomJSDriver() {
			DesiredCapabilities dcap = this.createDCapOptions();//creates CLI options
			PhantomJSDriver driver = new PhantomJSDriver(dcap);//creates new PhantomJS..exe process with given CLI options
			this.setDriverOptions(driver); //set other (non-CLI) options
			return driver;
		}
	}// end of class Options
	
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
				InputStream reader = SeleniumPluginPackage.class.getResourceAsStream(nativeBinarySource);
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
