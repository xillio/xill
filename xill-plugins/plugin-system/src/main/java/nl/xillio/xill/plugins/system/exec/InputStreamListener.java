package nl.xillio.xill.plugins.system.exec;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import nl.xillio.events.Event;
import nl.xillio.events.EventHost;

/**
 * This class will listen for lines and call an event when data is found
 */
public class InputStreamListener implements Runnable {
	private final BufferedReader input;
	private final EventHost<String> onLineComplete = new EventHost<>();
	private Thread thread;

	/**
	 * Create a new {@link InputStreamListener}
	 *
	 * @param input
	 *        the input
	 */
	public InputStreamListener(final InputStream input) {
		this.input = new BufferedReader(new InputStreamReader(input));
	}

	/**
	 * Start the thread
	 */
	public void start() {
		if (thread != null && thread.isAlive()) {
			throw new IllegalStateException("This listener is already running.");
		}

		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		boolean shouldStop = false;

		while (!shouldStop) {
			String line = "";
			try {
				line = input.readLine();
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (line == null) {
				shouldStop = true;
			} else {
				onLineComplete.invoke(line);
			}
		}
	}

	/**
	 * @return true if and only if the thread is running
	 */
	public boolean isAlive() {
		return thread != null && thread.isAlive();
	}

	/**
	 * This event is called whenever the reader reads a full line
	 * 
	 * @return the event
	 */
	public Event<String> getOnLineComplete() {
		return onLineComplete.getEvent();
	}
}
