package nl.xillio.xill.plugins.system.constructs;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;

/**
 * Returns a list of various pieces of information about the system
 */
public class InfoConstruct extends Construct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(() -> process(context));
	}

	private static MetaExpression process(final ConstructContext context) {

		LinkedHashMap<String, MetaExpression> values = new LinkedHashMap<>();

		getRobotInfo(values, context);
		getRuntime(values);
		getFileSystem(values);

		return fromValue(values);
	}

	private static void getFileSystem(final LinkedHashMap<String, MetaExpression> values) {
		List<MetaExpression> fileSystem = new ArrayList<>();

		for (File systemRoot : File.listRoots()) {
			LinkedHashMap<String, MetaExpression> rootInfo = new LinkedHashMap<>();
			rootInfo.put("path", fromValue(systemRoot.getAbsolutePath()));

			LinkedHashMap<String, MetaExpression> memory = new LinkedHashMap<>();
			memory.put("total", fromValue(systemRoot.getTotalSpace()));
			memory.put("free", fromValue(systemRoot.getFreeSpace()));
			memory.put("used", fromValue(systemRoot.getTotalSpace() - systemRoot.getFreeSpace()));
			rootInfo.put("storage", fromValue(memory));

			fileSystem.add(fromValue(rootInfo));
		}

		values.put("filesystem", fromValue(fileSystem));
	}

	private static void getRuntime(final LinkedHashMap<String, MetaExpression> values) {
		Runtime runtime = Runtime.getRuntime();

		values.put("availableprocessors", fromValue(runtime.availableProcessors()));

		LinkedHashMap<String, MetaExpression> memory = new LinkedHashMap<>();
		memory.put("free", fromValue(runtime.freeMemory()));
		memory.put("total", fromValue(runtime.totalMemory()));
		memory.put("max", fromValue(runtime.maxMemory()));
		memory.put("used", fromValue(runtime.totalMemory() - runtime.freeMemory()));
		values.put("memory", fromValue(memory));

	}

	private static void getRobotInfo(final LinkedHashMap<String, MetaExpression> values, final ConstructContext context) {
		values.put("robotpath", fromValue(context.getRobotID().getPath().getAbsolutePath()));
		values.put("rootrobotpath", fromValue(context.getRootRobot().getPath().getAbsolutePath()));
		values.put("projectpath", fromValue(context.getRobotID().getProjectPath().getAbsolutePath()));
	}
}
