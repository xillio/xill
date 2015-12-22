package nl.xillio.xill.debugging;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nl.xillio.plugins.XillPlugin;
import nl.xillio.xill.components.instructions.VariableDeclaration;
import xill.lang.xill.PluginCall;
import xill.lang.xill.Target;
import xill.lang.xill.UseStatement;

/**
 * This class represents a wrapper for various kinds of information that can be used for debugging
 */
public class DebugInfo implements nl.xillio.xill.api.DebugInfo {

	private Map<Target, VariableDeclaration> variables = new HashMap<>();
	private Map<PluginCall, XillPlugin> using = new HashMap<>();

	/**
	 * Add all information from into to this
	 * 
	 * @param info
	 */
	public void add(final DebugInfo info) {
		variables.putAll(info.getVariables());
		using.putAll(info.getUsing());
	}

	/**
	 * @return the variables
	 */
	public Map<Target, VariableDeclaration> getVariables() {
		return variables;
	}

	/**
	 * @param variables
	 *        the variables to set
	 */
	public void setVariables(final Map<Target, VariableDeclaration> variables) {
		this.variables = variables;
	}

	/**
	 * @return the using
	 */
	public Map<PluginCall, XillPlugin> getUsing() {
		return using;
	}

	/**
	 * @param using
	 *        the using to set
	 */
	public void setUsing(final Map<PluginCall, XillPlugin> using) {
		this.using = using;
	}

	/**
	 * @return a set of used plugins
	 */
	public Set<XillPlugin> getUsedPlugins() {
		return new HashSet<>(using.values());
	}

	public Target getTarget(VariableDeclaration declaration) {
		return variables.entrySet().stream()
				.filter(entry -> entry.getValue() == declaration)
				.map(Map.Entry::getKey)
				.findAny()
				.orElse(null);
	}
}
