package nl.xillio.migrationtool.gui;

/**
 * This interface represents a child component of a robot tab
 */
public interface RobotTabComponent {
	/**
	 * Initialize the component. After loading and building the dom
	 * 
	 * @param tab
	 */
	public void initialize(RobotTab tab);
}
