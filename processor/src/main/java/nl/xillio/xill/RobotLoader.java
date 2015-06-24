package nl.xillio.xill;

import java.io.File;
import java.io.IOException;

import nl.xillio.plugins.CircularReferenceException;
import nl.xillio.plugins.PluginLoader;
import nl.xillio.xill.api.NullDebugger;
import nl.xillio.xill.api.PluginPackage;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.api.errors.XillParsingException;
import nl.xillio.xill.constructs.System.SystemPluginPackage;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;
import org.eclipse.internal.xtend.xtend.parser.SyntaxError;

/**
 * This class can be used to run a robot
 */
class RobotLoader {
	private static final Logger log = Logger.getLogger(RobotLoader.class);

	/**
	 * Run a robot.<br/>
	 * Arguments: [robotFile] [Optional: projectFolder]
	 *
	 * @param args
	 * @throws CircularReferenceException
	 * @throws IOException
	 * @throws XS_ScriptException
	 * @throws SyntaxError
	 * @throws XillParsingException
	 */
	public static void main(final String[] args) throws CircularReferenceException, IOException {
		if (args.length == 0) {
			System.out.println("No file was provided.");
			System.exit(0);
		}

		File robotFile = new File(args[0]);
		PluginLoader<PluginPackage> pluginLoader = PluginLoader.load(PluginPackage.class);

		pluginLoader.addToQueue(new SystemPluginPackage());

		pluginLoader.load();

		XillProcessor processor = new XillProcessor(robotFile.getParentFile(), robotFile, pluginLoader, new NullDebugger());

		Thread thread = new Thread(() -> {

			try {
				processor.compile();

				StopWatch sw = new StopWatch();

				sw.start();

				InstructionFlow<MetaExpression> result = processor.getRobot().process(processor.getDebugger());

				sw.stop();

				if (result.hasValue()) {
					log.info("Result: " + result.get());
				} else {
					log.info("No return value.");
				}

				log.info("Completed in " + sw.toString());

			} catch (IOException | XillParsingException | RobotRuntimeException e) {
				e.printStackTrace();
			}
		});

		// Run the robot
		thread.start();
	}
}
