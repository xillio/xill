import nl.xillio.exiftool.ExifTool;
import nl.xillio.exiftool.ProcessPool;

import java.io.File;
import java.util.Map;

public class Test {

    public static void main(String... args) {
        ProcessPool pool = ExifTool.buildPool();

        try(ExifTool tool = pool.getAvailable()) {

            Map<String, String> data = tool.readFields(new File("D:\\Libraries\\OneDrive\\Branding\\"));

            data.forEach((key,value) -> System.out.println(key + " >> " + value));

        }
    }
}
