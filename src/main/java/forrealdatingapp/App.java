package forrealdatingapp;

import java.io.IOException;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class App extends Application{
    
    static LoginWindow loginWindow = new LoginWindow();
    static MatchesPage matchesPage = new MatchesPage();
    
    String token_id;
    static boolean isTokenOnline = false;
    @Override
    public void start(Stage primaryStage) throws IOException{
       
        loginWindow.showLoginWindow(primaryStage, token_id);

    }
    @Override
    public void stop() {
        TokenManager tokenManager = new TokenManager();
        if (token_id != null && !isTokenOnline)
            tokenManager.clearToken(token_id);  // Clear the token when the app stops
        ChatZone.closeConnection();
        Platform.exit();  // Exit the JavaFX application
    }
    static <T extends Pane> void BackToLoginBtn(T div, Stage stage){
        Button backButton = new Button("back to login/sign up screen");
        backButton.setOnAction((actionEvent) -> {
        loginWindow.showLoginWindow(stage,null);

        });
        div.getChildren().add(backButton);
        

        


    }
    public static String getEnv(String key, String defaultValue) {
        String value = "";
        try {
            Dotenv env = Dotenv.load();
            value = env.get(key);
            
        } catch (Exception e) {
            value = System.getenv(key);
         }
        // Try to get value from dotenv
        
        // Check if value is null, fallback to system environment
        if (value.isEmpty()) {
            value = System.getenv(key);
        }
        
        // If still null, return default value
        if (value.isEmpty()) {
            value = defaultValue;
        }
        System.out.println(value);
        
        return value;
    }
    
    
    public static void main(String[] args) throws Exception {
        // // delete later
        // TokenManager tokenManager = new TokenManager();
        // tokenManager.clearToken();

        launch(args);
        
    }
}
