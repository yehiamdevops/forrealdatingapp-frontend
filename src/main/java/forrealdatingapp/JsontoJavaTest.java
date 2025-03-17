package forrealdatingapp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsontoJavaTest {
    public static void main(String[] args) {
        /*when to use io exception:
        You should use it in situations where your code interacts with resources outside of your control, like:

            Files
            Network streams
            Databases (when using JDBC)
            I/O devices
        
         */
        Path jsonPath = Paths.get("user.json");
        // System.out.println(jsonPath);
        try{
            String jsonContent = Files.readString(jsonPath);
            // System.out.println("json content below: v");
            // System.out.println(jsonContent);
            ObjectMapper om = new ObjectMapper();
            
            /*deserialize json to any class i want
            it can be a class that i made or a collection or whatever just need to use .class
            */
            Map<String, Object> jsonUser = om.readValue(jsonContent, new TypeReference<Map<String, Object>>(){});
            
            List<Map<String, Object>> friends =  (List<Map<String, Object>>) jsonUser.get("friends");
            // System.out.println(friends.get(0).get("name"));
            if((boolean)jsonUser.get("isProgrammer"))
                System.out.println("you a programmer gz!!!!");
            
            
            

            
            


            
            
        }
        catch(IOException e){
            //stack trace is basically a stack of errors leading to the source
            System.out.println(e.getLocalizedMessage());
        }

        
    }
    
    
    
}
