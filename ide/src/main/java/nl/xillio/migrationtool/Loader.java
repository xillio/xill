package nl.xillio.migrationtool;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import nl.xillio.xill.api.Xill;

/**
 * Launcher class, is used to launch processors in their own threads, facilitates a simple Log, and provides commandline running.
 */
public class Loader implements nl.xillio.contenttools.PluginPackage {
	static {
		
	}

	private static final Logger log = LogManager.getLogger();
	/**
	 * A manually maintained history of the program...
	 */
	public static final String[][] HISTORY =
					new String[][] {
									// Version Date Comment
									{"2.6.3", "11-06-2015", "Replace Microsoft SQL Server driver by JTDS"},
									{"2.6.2", "10-06-2015", "Add Oracle JDBC driver to build"},
									{"2.6.1", "08-06-2015", "Fix Elastic Search connection bug"},
									{"2.6.0", "21-05-2015", "New GUI, new logging system, major rework of backend"},
									{"2.5.1", "07-04-2015", "Fix bugs in breakpoints, round(), exiftool() and Elastic Search functions. Remove inconsitencies in help files."},
									{"2.5.0", "19-03-2015", "Added support for MongoDB, Elastic Search and shortcut operators ++, --, += and -=. Numerous bugfixes."},
									{"2.4.4", "12-02-2015", "Added text extraction for any type with the textfromdocument() and metadatafromdocument() functions."},
									{"2.4.3", "31-12-2014", "Added code completion using [Ctrl] + [Space]. Fixed bug where some database resultsets where not correctly closed."},
									{"2.4.2", "31-12-2014", "Added connectivity for databases based on service name instead of SID for Oracle connector."},
									{"2.4.1", "04-12-2014", "Fixed issues with the debugger related to error-reporting in general and stepping through subrobots. Several bugfixes and minor performance improvements."},
									{"2.4.0", "31-10-2014", "Added Migration manager integration. Numerous small and a few major bug fixes. Interface improvements."},
									{"2.3.5", "05-07-2014", "Fixes for SQLServer connector"},
									{"2.3.4", "05-07-2014", "Fixed buttonbar not getting updated properly"},
									{"2.3.3", "25-06-2014", "Updated exiftool to work cross-platform"},
									{"2.3.2d", "25-06-2014", "Fixed issue with hash collisions in query caching of the database. Fixed incorrect md5 hashing."},
									{"2.3.2c", "24-06-2014", "Fixed issue character encoding going awry when storing data in the database."},
									{"2.3.2b", "18-06-2014", "Fixed issue with getobject() and storeobject() sometimes querying incorrect table."},
									{"2.3.2", "16-06-2014", "Fixed critical issue with getobject() and storeobject() that randomly returned null."},
									{"2.3.1", "12-06-2014", "Fixed issue with storeobject() when using multiple keys. Fixed issue with cached queries in storeobject(). Fixed replace() when using the replacefirst option. Fixed out-of-memory issue with MD5 on large files."},
									{"2.3.0", "11-06-2014", "Java 8 release (java 7 no longer supported). Added MSSQL support, fixes for regex/replace/matches, fixed issue with duplicate robotnames, listfolders/listfiles now sorts output, small improvements for debugging code run by evaluate(), items can now be added to lists using: mylist[] = \"newentry\";,  fxnodes are now shown in the preview properly."},
									{"2.2.2", "04-06-2014", "Fixed bug in storeobject() function."},
									{"2.2.1", "23-05-2014", "Fixed bug in getobject() function."},
									{"2.2.0", "23-05-2014", "Small fixes, new official build."},
									{"2.1.19", "12-05-2014", "Added timeout to regexes, complete new DB abstraction layer implemented."},
									{"2.1.18", "02-05-2014", "Added dynamic arguments functionality, eg. log(text=\"Hello world\", loglevel=\"warning\");. Significantly improved syntax error reports."},
									{"2.1.17", "30-04-2014", "Fixed issue with decimal values being rounded using setcell. Maximal decimal value before rounding occurs: 9999999999.99, maximum non-decimal value before rounding occurs: 999999999999999"},
									{"2.1.16", "17-04-2014", "Hotfix for issues with concatenating strings in general, concatenating strings in lists in specific. Also fixed issue with lists not refreshing in debugger when stepping through code. "},
									{"2.1.15", "16-04-2014", "Improved handling of variables with large amounts of data in the debugger."},
									{"2.1.14", "15-04-2014", "Fixed default namespace issue with fxxpath. Greatly improved string concatenation performance. Preview now denies showing supersize variables and limits lists to first 10 entries for xpath results to prevent GUI locking up. Fixed xml preview missing the root node. Updated documentation. Added curl function."},
									{"2.1.13", "09-04-2014", "Added xsdcheck function. Added releasenotes popup on application start (only shown first time)."},
									{"2.1.12", "03-04-2014", "Added support for setting the timezone of the provided date in routine date()."},
									{"2.1.11", "02-04-2014", "Fixed issue with large numbers when setting excel columns. Fixed issue with SQL values being incorrectly converted."},
									{"2.1.10", "25-03-2014", "Updated md5, loaddata, savedata and added binaryvariable. Updated webclient to fully reset on call."},
									{"2.1.9", "19-03-2014", "Function countsheetrows has been removed and rows and columns are now automatically updated with setcell."},
									{"2.1.8", "19-03-2014", "Workbook now extends list. Added functionality to get number of rows in a sheet. Code highlighting fix by titus in xillio.js"},
									{"2.1.7", "19-03-2014", "Implemented the possibility to directly create Excel files. Fixed issues with getcell() functionality and general Excel stuff. Fixed issue with listfiles() and listfolders() where folders that require permission would stop the robot."},
									{"2.1.6", "24-02-2014", "Fixed issue with global/list index interference. Incorrect database caching solved. Fixed xmltolist. Added a robot-info entries to systeminfo(). Priority change for global/local scope handling. Unsaved robot now properly highlights line on pause."},
									{"2.1.5", "14-02-2014", "Valentine's edition! true|false highlighting improved. Added xlsx support. Cursor now shows insert/overwrite status. Variable view consistent. Database connection retries after server-disconnect. Auto focus editor. Added FileInfo construct. Added namespace support for XPath."},
									{"2.1.4", "06-02-2014", "Fixed stability issue, patched two memory leaks."},
									{"2.1.3", "05-02-2014", "Implemented significant performance boost at runtime at cost of slightly longer compile time. Implemented XPath 2.0 support. Added commandline option to validate robots. Minor change and updated documentation regarding runprogram()."},
									{"2.1.2", "28-01-2014", "Bugfix for loading UTF-8 file with BOM. Added optional encoding parameter to savedata "},
									{"2.1.1", "27-01-2014", "Bugfix for non-breaking space not getting processed properly by trim()"},
									{"2.1.0", "23-01-2014", "Fixed numerous bugs, implemented several feature requests, usability improvements and performance issues."},
									{"2.0.14", "06-01-2014", "Made connectionlist for databases static to fix the issue with \"too many connections\" when running a large amount of robots on the server."},
									{"2.0.13b", "03-01-2014", "Fixed an issue where callbot didn't register IOAbstractionLayer on the child robot. Fixed issue with licenses not working on Linux."},
									{"2.0.12b", "12-12-2013", "Fixed an issue where callbot didn't register the project path on the child robot"},
									{"2.0.11b", "12-12-2013", "Fixed an issue with the upload to server functionality, where the path was actually sent as the full path"},
									{"2.0.10b", "11-12-2013", "Implemented 'upload to server' functionality on project right click. Fixed issue with adding projects where hitting cancel would still add the project. Also, adding a project now checks if there is already a project with the name and/or folder."},
									{"2.0.9b", "07-11-2013", "Fixed a few small UI related issues."},
									{"2.0.8b", "01-11-2013", "Fixed breakpoint issues with includes."},
									{"2.0.7b", "30-10-2013", "Fixed issues with: Variable view not updating properly, list indexes, double variable names, project path in commandline, breakpoints, concatenating multi level lists. Fixed big performance hog with parsing new robots. "},
									{"2.0.6b", "25-10-2013", "Added systeminfo, datediff, indexof, fixed issue with concatenating lists, added option to split, fixed some documentation, added option to replace, fixed issue with evaluate, fixed comment readability, and more :)"},
									{"2.0.5b", "24-10-2013", "Fixed issue with return() statement, fixed issue with array lists. Added new date functions, significant changes to dateformat (also renamed to datetostring). Various other fixes."},
									{"2.0.4b", "18-10-2013", "Added runtime indicator icons to tabs, console now switches with robot. Various fixes."},
									{"2.0.3b", "15-10-2013", "Lots of small fixes."},
									{"2.0.2b", "15-10-2013", "Activated license module. Fixed serious memoryleak, fixed a few other minor bugs."},
									{"2.0.1b", "09-10-2013", "Major performance improvements when running in debugger."},
									{"2.0.b", "07-10-2013", "Stable beta version. Extractionbrowser fully operational. Various fixes, function renaming. Reverted the variable scope to normal."},
									{"2.0.0a2", "30-09-2013", "Alfa 2. Major fixes regarding evaluation. Various bugfixes for constructs."},
									{"2.0.0", "20-09-2013", "Alfa version. Complete overhaul of the application\n- New UI based on JavaFX (Design by Ruben)\n- In-application documentation (Titus)\n- Extraction browser (Xavier)\n- Inline code evaluation\n- New code editor\n- Variable scope is now function-wide, eliminating the need to initalise variables before entering a loop or conditional statement\n- Added projects, workspace is saved at end of session, use of settings database instead of ini file                     "},
									{"1.6.4", "23-07-2013", "Added interactive evaluation functionality (no interface for it yet), fixed issue with string functions in xpath, fixed various minor things"},
									{"1.6.3", "28-06-2013", "Reorganised packages. Added include construct that allows to directly call methods in library robots. Complete rework of list processing (now done at compile time rather than runtime) and added support for unlimited inline list notation with dots (e.g. a.b.c.d). Fixed list-merge function. Implemented central configuration utility for future use with the new GUI.  "},
									{"1.6.2", "02-05-2013", "New implementation of runprogram. New implementation of Undo / Redo. Fixed issue with loadpage on local files. Added XML handling constructs for live DOM transformations: removenode (renamed from 'remove'), copynode, movenode, addsubnode, replacenode. Fixed issue with faulty default settings for tidy that resulted in empty string returned. Removed some debug messages. Less logmessages from libraries in console."},
									{"1.6.1", "16-04-2013", "Rewrite of variable assignment and specifically list handling (Lists now parsed at compile time rather than at run time, which also fixes the bug where a dot in a list index caused issues). Various small fixes and improvements."},
									{"1.6.0", "05-04-2013", "Rewrite of Lexer/Tokenize architecture (now gets loaded from xml definition). Huge performance improvement for styler. Added dynamic styling of matching braces. Changed to Log4J logging. Added Added html frame support."},
									{"1.5.1", "07-02-2013", "Various small bugfixes"},
									{"1.5.0", "07-02-2013", "Added lazy evaluation of binary expressions, added operator prioritising (common > comparison > binary)   "},
									{"1.4.11", "01-02-2013", "Added SQLite support"},
									{"1.4.10", "01-02-2013", "Tab/Shift-tab bugfix on lines > ~1000 in Lexer, function getCharPosition. (Remco)", " "},
									{"1.4.9", "26-01-2013", "Removed surplus \"next\" button from debugger"},
									{"1.4.8", "31-12-2012", "Added simple deletefile function"},
									{"1.4.7", "27-12-2012", "Updated the replace routine with the ability to only replace the first occurrence (Remco)"},
									{"1.4.6", "06-12-2012", "Fixed unwanted value trimming in getcell(). Added evaluate() routine."},
									{"1.4.5", "29-11-2012", "Updated download() to work inside session."},
									{"1.4.4", "27-11-2012", "Debugger/robot changes: Renamed \"Next\" button to \"Step Into\". Added \"Step Over\" button. Updated libraries."},
									{"1.4.3", "26-11-2012", "Bugfixes: DownloadConstruct now closes stream correctly, SQLResult now takes \"AS\" close into account, trim construct handles string input from external files correctly."},
									{"1.4.2", "16-11-2012", "Fixes for RawHTTP (Content-Type header is now set correctly), and improved support for pageinfo()"},
									{"1.4.1", "15-11-2012", "Fixes & documentation for the webclient"},
									{"1.4.0", "10-11-2012", "Complete rewrite of the script engine, fixed numerous bugs with the script parsing"},
									{"1.3.3", "18-10-2012", "Added support for cookie handling: setcookie(), removecookie(). Cookies can be obtained through the pageinfo construct."},
									{"1.3.2", "16-10-2012", "Fixed issue with asynchronous javascript loading"},
									{"1.3.1", "09-10-2012", "Fixed bug with prefixes, fixed tidy, fixed trim, fixed xml output, fixed pageinfo for binaries, fixed bug for absolute urls, fixed bug with version construct, updated savedata construct to handle more datatypes. "},
									{"1.3.0", "24-09-2012", "Updated libraries, implemented proper NTLM authentication, added support for prefix operators."},
									{"1.2.10", "20-09-2012", "Removed debug logging from runprogram construct, fixed bug when breaking out of foreach-construct"},
									{"1.2.9", "28-08-2012", "Resolved regex issue with literals, fixed issue with runprogram."},
									{"1.2.7", "15-08-2012", "Birthday present! Proper file loading for the LoadData construct."},
									{"1.2.6", "12-06-2012", "Fixed bug with ampersanddecode not recognising hexadecimal xml entities."},
									{"1.2.5", "07-06-2012", "Fixed bug with return statement not working in specific foreach situations. "},
									{"1.2.4", "18-05-2012", "Small fixes. Non XML/HTML items will now have metadata available via a list when obtained, automatically downloaded, and can be saved using the savedata() function."},
									{"1.2.2", "07-05-2012", "Bugfix in new tabbing-routine, added constructs, string comparison added."},
									{"1.2.1", "05-05-2012", "Minor fixes, selection now persists when tabbing a block (Wouter), fixed firstline shifttabbing issue (Ernst)"},
									{"1.2.0", "30-04-2012", "Fixed issue with backslashes in regexes.\nAdded word distance function which uses Damereau-Levenshtein\nAdded math power function (x ^ n) to calculate the n-th power of x.\nAdded type function to determine the type of a variable.\nUpdated split function to skip empty entries.\nFixed erratic setting of breakpoints, now linebased (Wouter)\nTwo lists can now be merged using the '+' operator"},
									{"1.1.5", "27-04-2012", "Various minor fixes, including incorrect linenumber issue (Wouter)."},
									{"1.1.4", "10-04-2012", "Various minor fixes."},
									{"1.1.3", "03-04-2012", "Fixed bug with processing large numbers, fixed formatting bug. Added xpath cheat sheet, small regex performance improvements. "},
									{"1.1.2", "29-03-2012", "Added support for JSON parsing. Also lists will now be outputted as true JSON. A minor fix regarding parsing numbers. Added more complex list assignment support."},
									{"1.1.1", "28-03-2012", "Added optional and configurable timeout for queries. Few bugfixes."},
									{"1.1.0", "26-03-2012", "Added URL en/decoding, several minor improvements"},
									{"1.0.9", "20-03-2012", "Added base64 functions, close-file shortkey, and a few small bugfixes."},
									{"1.0.8", "14-03-2012", "Added a few extra string-handling constructs. Improved XML-error handling. Added functionality that strips all control characters from the xml stream before parsing."},
									{"1.0.7", "07-03-2012", "Created workaround for HTMLUnit bug which does not allow XPaths to be executed on XML documents. Several minor improvements."},
									{"1.0.6", "03-03-2012", "Fixed bug that occurred with the resourcemanager in combination with the debugger. Fixed several minor issues."},
									{"1.0.5", "28-02-2012", "Added repeat string function, a few minor bugfixes and fixes in the statepanel."},
									{"1.0.4", "24-02-2012", "More GC performance tweaks & fixes "},
									{"1.0.3", "22-02-2012", "Added resource manager for State objects, for better GC performance, small bug fixes"},
									{"1.0.0", "21-02-2012", "Added instruction stack panel. Changed GUI statepanel, so it only updates on pause (better GC performance). "},
									{"0.9.1", "20-02-2012", "Minor bug fixes, changed a few constructs to handle VOID arguments"},
									{"0.9.0", "17-02-2012", "Lots of small bug fixes"},
									{"0.7.0", "18-01-2012", "Beta release of full featured GUI"},
									{"0.5.0", "05-11-2011", "Alpha release of scripting engine "}
					};

	/**
	 * The GUI's current official version.
	 */
	public static final String SHORTVERSION = Loader.class.getPackage().getImplementationVersion() == null ? "dev" : Loader.class.getPackage().getImplementationVersion();

	/**
	 * The release date of the official version.
	 */
	public static final String VERSIONDATE = HISTORY[0][1];

	/**
	 * Just the version number + date.
	 */
	public static final String LONGVERSION = SHORTVERSION + ", " + VERSIONDATE;

	private static Xill xill;

	@Override
	public void start(final Stage primaryStage, final Xill xill) {
		log.info("Starting Contentools IDE");
		Loader.xill = xill;
		Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

		try (InputStream image = this.getClass().getResourceAsStream("/icon.png")) {
			if (image != null) {
				primaryStage.getIcons().add(new Image(image));
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		Parent root;
		try {
			Font.loadFont(this.getClass().getResourceAsStream("/fonts/Glober SemiBold.ttf"), 10);
			Font.loadFont(this.getClass().getResourceAsStream("/fonts/UbuntuMono Regular.ttf"), 10);

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/contenttools.fxml"));
			root = loader.load();

			Scene scene = new Scene(root, 1024, 768);

			primaryStage.setScene(scene);

			// Set window to max size (native maximize is not available)
			Screen screen = Screen.getPrimary();
			Rectangle2D bounds = screen.getVisualBounds();

			primaryStage.setX(bounds.getMinX());
			primaryStage.setY(bounds.getMinY());
			primaryStage.setWidth(bounds.getWidth());
			primaryStage.setHeight(bounds.getHeight());

		} catch (IOException e) {
			System.err.println("Loader.initGUI(): Fatal error occurred during launch: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}

		primaryStage.setTitle("xillio content tools - " + LONGVERSION);
		primaryStage.getScene().lookup("#apnRoot").setVisible(false);
		primaryStage.show();

		Platform.runLater(() -> primaryStage.getScene().lookup("#apnRoot").setVisible(true));
	}

	@Override
	public Object serve() {
		// Nothing to serve
		return null;
	}

	/**
	 * Shortcut to load FXML
	 *
	 * @param resource
	 * @param controller
	 * @return The node represented in the FXML resource
	 * @throws IOException
	 */
	public static Node load(final URL resource, final Object controller) throws IOException {
		FXMLLoader loader = new FXMLLoader(resource);
		loader.setClassLoader(controller.getClass().getClassLoader());
		loader.setController(controller);
		return loader.load();
	}

	@Override
	public void load(final nl.xillio.contenttools.PluginPackage[] dependencies) {}

	/**
	 * @return The xill implementation this was initialized with
	 */
	public static Xill getXill() {
		return xill;
	}
}
