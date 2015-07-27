package nl.xillio.xill.plugins.system.services.info;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.inject.Singleton;

import nl.xillio.xill.services.PropertiesProvider;
import nl.xillio.xill.services.XillService;

/**
 * This class is a {@link XillService} that provides information about the current {@link Runtime}
 *
 */
@Singleton
public class RuntimeInfo implements PropertiesProvider {
	private final Map<String, Object> properties = new LinkedHashMap<>();
	private final int availableProcessors;
	private final long freeMemory;
	private final long totalMemory;
	private final long maxMemory;
	private final long usedMemory;

	/**
	 * Create a new {@link RuntimeInfo} from the current {@link Runtime}
	 */
	public RuntimeInfo() {
		this(Runtime.getRuntime());
	}

	/**
	 * Create a new {@link RuntimeInfo} from a {@link Runtime}
	 *
	 * @param runtime
	 *        the {@link Runtime}
	 */
	public RuntimeInfo(final Runtime runtime) {
		availableProcessors = runtime.availableProcessors();
		freeMemory = runtime.freeMemory();
		totalMemory = runtime.totalMemory();
		maxMemory = runtime.maxMemory();
		usedMemory = totalMemory - freeMemory;

		Map<String, Object> memory = new LinkedHashMap<>();
		memory.put("free", freeMemory);
		memory.put("total", totalMemory);
		memory.put("max", maxMemory);
		memory.put("used", usedMemory);

		properties.put("availableProcessors", availableProcessors);
		properties.put("memory", memory);
	}

	@Override
	public Map<String, Object> getProperties() {
		return properties;
	}

	/**
	 * @return the availableProcessors
	 */
	public int getAvailableProcessors() {
		return availableProcessors;
	}

	/**
	 * @return the freeMemory
	 */
	public long getFreeMemory() {
		return freeMemory;
	}

	/**
	 * @return the totalMemory
	 */
	public long getTotalMemory() {
		return totalMemory;
	}

	/**
	 * @return the maxMemory
	 */
	public long getMaxMemory() {
		return maxMemory;
	}

	/**
	 * @return the usedMemory
	 */
	public long getUsedMemory() {
		return usedMemory;
	}

}
