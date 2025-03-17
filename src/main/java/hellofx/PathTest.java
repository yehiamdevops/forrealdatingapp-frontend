package hellofx;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathTest {
    public static void main(String[] args) {
        Path relativePath = Paths.get("file.txt");
        //to absolute path method helps showing what directory java is working on
        System.out.println("Looking for file at: " + relativePath.toAbsolutePath());
        System.out.println(Files.exists(relativePath));
            
         
    }

    
}
