package nl.xillio.migrationtool.ElasticConsole;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

/**
 * This {@link AppenderSkeleton} logs all messages to the {@link ESConsoleClient}
 *
 */
public class ESAppender extends AppenderSkeleton {
	private static final String ROBOT_PACKAGE_PREFIX = "robot.";
	private static int order = Integer.MIN_VALUE;
	
	@Override
	public void close() {
	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

	@Override
	protected void append(LoggingEvent event) {
		
		//Only log this to the ES client if the package name is correct
		String name = event.getLoggerName();
		
		if(name.startsWith(ROBOT_PACKAGE_PREFIX)) {
			String robotId = name.substring(ROBOT_PACKAGE_PREFIX.length());
			ESConsoleClient.getInstance().log(
					robotId, 
					event.getLevel().toString(), 
					event.getTimeStamp(), 
					order++,
					event.getMessage().toString());
			
			
		}
	}

}
