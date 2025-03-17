package hellofx;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import io.github.cdimascio.dotenv.Dotenv;

public class CloudinaryUtils {
  
    public static String Upload(File file) {
        try {
            Dotenv dotenv = Dotenv.load();
            Cloudinary cloudinary = new Cloudinary(dotenv.get("CLOUDINARY_URL"));
            Map params = ObjectUtils.asMap(
                    "overwrite", true,
                    "resource_type", "image"
            );
            Map uploadResult = cloudinary.uploader().upload(file, params);
            return (String) uploadResult.get("secure_url");
            
        } catch (IOException ex) {
            return null;
        }
    }
}
