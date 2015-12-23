import nl.xillio.exiftool.*;
import org.apache.commons.lang3.time.StopWatch;

import java.io.IOException;
import java.nio.file.Paths;

public class Test {




    public static void main(String... args) throws IOException, InterruptedException {

        try (ProcessPool pool = ExifTool.buildPool()) {
            runTest(pool, 1);
        }
    }

    private static void runTest(ProcessPool pool, int numberOfOpenProcesses) throws IOException {

        try (ExifTool tool = pool.getAvailable()) {
            StopWatch sw = new StopWatch();
            sw.start();
            ExifReadResult data = tool.readFieldsForFolder(Paths.get("D:\\Libraries\\OneDrive\\Afbeeldingen\\"), true);
            int count = 0;
            int totalTagCount = 0;

            if (numberOfOpenProcesses > 1) {
                // Test the process management
                runTest(pool, numberOfOpenProcesses - 1);
            }
            while (data.hasNext()) {
                count++;
                totalTagCount += data.next().size();
            }

            sw.stop();
            System.out.printf("Parsing done in %s.%nParsed %d files.%nThese contained a total of %d tags.%n", sw, count, totalTagCount);

        }
    }
}
