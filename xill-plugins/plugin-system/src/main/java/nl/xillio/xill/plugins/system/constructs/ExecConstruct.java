package nl.xillio.xill.plugins.system.constructs;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * Runs an application and waits for it to complete
 */
public class ExecConstruct extends Construct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(ExecConstruct::process, new Argument("program"), new Argument("arguments", emptyList()), new Argument("directory", NULL));
	}

	@SuppressWarnings("unchecked")
	private static MetaExpression process(final MetaExpression program, final MetaExpression arguments, final MetaExpression directory) {
		assertType(arguments, "arguments", LIST);
		
		//Initialize builder
		ProcessBuilder processBuilder = new ProcessBuilder(program.getStringValue());
		
		//Set working directory
		if (!directory.isNull()) {
			processBuilder.directory(new File(directory.getStringValue()));
		}
		
		//Set additional commands
		processBuilder.command(((List<MetaExpression>)arguments.getValue()).stream().map(expression -> expression.getStringValue()).collect(Collectors.toList()));

		// Start
		Process process;
		try {
			process = processBuilder.start();
		} catch (IOException e) {
			throw new RobotRuntimeException("Failed to run " + program.getStringValue() + ": " + e.getMessage(), e);
		}

		// Listen to output
		InputStreamReader processIn = new InputStreamReader(process.getInputStream());

		String output = "";
		char[] buffer = new char[255];
		while (process.isAlive()) {
			try {
				if (processIn.ready()) {
					processIn.read(buffer);
					System.out.println(buffer);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return NULL;
	}
}
