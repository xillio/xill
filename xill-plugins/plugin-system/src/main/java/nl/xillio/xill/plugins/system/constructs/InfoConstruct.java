package nl.xillio.xill.plugins.system.constructs;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.system.services.info.SystemInfoService;

/**
 * Returns a list of various pieces of information about the system
 */
public class InfoConstruct extends Construct {

	@Inject
	private SystemInfoService infoService;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(() -> process(context, infoService));
	}

	static MetaExpression process(final ConstructContext context, final SystemInfoService infoService) {

		Map<String, Object> values = new HashMap<>();

		values.put("filesystem", infoService.getFileSystemInfo().getRootProperties());
		values.putAll(infoService.getRuntimeInfo().getProperties());
		values.putAll(infoService.getRobotRuntimeInfo(context).getProperties());

		return parseObject(values);
	}
}
