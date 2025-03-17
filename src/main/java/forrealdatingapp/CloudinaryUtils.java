package forrealdatingapp;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;


public class CloudinaryUtils {
  
    public static String Upload(File file) {
        try {
            
            Cloudinary cloudinary = new Cloudinary(System.getenv("CLOUDINARY_URL"));
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
