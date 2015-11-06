package nl.xillio.migrationtool;

import me.biesaart.utils.Log;
import org.slf4j.Logger;

/**
 * This thread will wait for 5 seconds and then kill the application
 */
public class ApplicationKillThread extends Thread {
    private static final Logger LOGGER = Log.get();

    private ApplicationKillThread() {
        super("ApplicationKillThread");
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            synchronized (this) {
                this.wait(5000);
            }
        } catch (InterruptedException e) {
            System.out.println("Application was closed before it had to be force killed");
            return;
        }

        System.err.println("Forcing application to close by killing all threads!");
        System.exit(0);
    }

    public static void exit() {
        ApplicationKillThread thread = new ApplicationKillThread();
        thread.start();
    }
}
