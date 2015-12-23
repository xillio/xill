import nl.xillio.exiftool.ExifReadResult;
import nl.xillio.exiftool.ExifTags;
import nl.xillio.exiftool.ExifTool;
import nl.xillio.exiftool.ProcessPool;
import org.apache.commons.lang3.time.StopWatch;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

public class Test {

    public static void main(String... args) throws IOException {
        ProcessPool pool = ExifTool.buildPool();

        try(ExifTool tool = pool.getAvailable()) {

            StopWatch sw = new StopWatch();
            sw.start();
            ExifReadResult data = tool.readFieldsForFolder(Paths.get("\\\\BENDER\\XillioShared\\Xillio Events"), true);

            while(data.hasNext()) {
                ExifTags tags = data.next();
                tags.forEach((key,value) -> System.out.println(key + " >> " + value));
            }
            sw.stop();
            System.out.println(sw);

        }
    }
}
