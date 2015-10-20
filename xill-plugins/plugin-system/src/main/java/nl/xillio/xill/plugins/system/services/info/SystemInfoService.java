package nl.xillio.xill.plugins.system.services.info;

import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.services.XillService;

/**
 * This interface represents a service that provides system information
 */
public interface SystemInfoService extends XillService {
	/**
	 * Get properties related to the current filesystem and the available storage
	 *
	 * @return the {@link FileSystemInfo}
	 */
	public FileSystemInfo getFileSystemInfo();

	/**
	 * Get information related to the current {@link Runtime}
	 *
	 * @return the {@link RuntimeInfo}
	 */
	public RuntimeInfo getRuntimeInfo();

	/**
	 * Get information related to the current {@link RobotRuntimeInfo}
	 *
	 * @param context
	 *        the context
	 *
	 * @return the {@link RobotRuntimeInfo}
	 */
	public RobotRuntimeInfo getRobotRuntimeInfo(ConstructContext context);
}
