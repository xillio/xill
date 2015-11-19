package nl.xillio.migrationtool;

import me.biesaart.utils.Log;
import org.slf4j.Logger;

/**
 * This thread will wait for 5 seconds and then kill the application
 */
public class ApplicationKillThread extends Thread {


    private ApplicationKillThread() {
        super("ApplicationKillThread");
        setDaemon(true);
    }

    @Override
    @SuppressWarnings("squid:S1147") // use of System.exit
    public void run() {
        try {
            sleep(5000);
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
