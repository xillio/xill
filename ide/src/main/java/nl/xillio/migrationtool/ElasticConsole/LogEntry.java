package nl.xillio.migrationtool.ElasticConsole;

import nl.xillio.migrationtool.ElasticConsole.ESConsoleClient.LogType;
import javafx.beans.property.SimpleStringProperty;

public class LogEntry {
	//These are used by the cell value factories from the console tableview
	private final SimpleStringProperty showTime, showType, line;
	
	private final boolean newLine;
	private final LogType type;
	
	/**
	 * Create a new log entry.
	 * @param time The time of the entry.
	 * @param type The type of the entry (info, debug...)
	 * @param line The message of the entry (a single line).
	 * @param newLine Whether this entry is a new line.
	 */
	public LogEntry(String time, LogType type, String line, boolean newLine) {
		//Visible attributes, empty for new lines
		this.showTime = new SimpleStringProperty(newLine ? "" : time);
		this.showType = new SimpleStringProperty(newLine ? "" : type.toString());
		
		this.type = type;
		this.line = new SimpleStringProperty(line);
		this.newLine = newLine;
	}
	
	/**
	 * @return The type of this entry.
	 */
	public LogType getType() {
		return type;
	}
	/**
	 * @return The message (line) of this entry.
	 */
	public String getLine() {
		return line.get();
	}
	/**
	 * @return The time of this entry, or an empty string if this is a new line.
	 */
	public String getShowTime() {
		return showTime.get();
	}
	/**
	 * @return The type of this entry, or an empty string if this is a new line. 
	 */
	public String getShowType() {
		return showType.get();
	}
	/**
	 * True if this line is a new line, false if this is the first line.
	 * @return Whether this line is a new line.
	 */
	public boolean isNewLine() {
		return newLine;
	}
}
