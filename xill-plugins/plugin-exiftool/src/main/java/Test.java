import nl.xillio.exiftool.ExifTool;
import nl.xillio.exiftool.ProcessPool;
import nl.xillio.exiftool.query.ExifReadResult;
import nl.xillio.exiftool.query.ExifTags;
import nl.xillio.exiftool.query.Projection;
import org.apache.commons.lang3.time.StopWatch;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

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
            Projection projection = new Projection();
            //projection.put("megaPixels", true);
            ExifReadResult data = tool.readFieldsForFolder(Paths.get("D:\\Libraries\\OneDrive\\Afbeeldingen\\Wallpapers"), projection);
            int count = 0;
            int totalTagCount = 0;

            if (numberOfOpenProcesses > 1) {
                // Test the process management
                runTest(pool, numberOfOpenProcesses - 1);
            }
            while (data.hasNext()) {
                ExifTags tags = data.next();
                count++;
                totalTagCount += tags.size();
                System.out.println("\n" + tags.get("File Path"));
                for (Map.Entry<String, String> entry : tags.entrySet()) {
                    System.out.println("\t" + entry.getKey() + ": " + entry.getValue());
                }
                System.out.println();
            }

            sw.stop();
            System.out.printf("Parsing done in %s.%nParsed %d files.%nThese contained a total of %d tags.%n", sw, count, totalTagCount);

        }
    }
}
