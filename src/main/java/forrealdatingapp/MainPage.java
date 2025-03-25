package forrealdatingapp;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainPage {
private static final ObjectMapper om = new ObjectMapper();
private static int Count = 0;
private static boolean  UsersDetectad;
private  static User next;
private static Queue<User> users;
    void showMainPage(Stage stage, String _id) {
        if (ChatZone.chatArea == null){
                ChatZone.chatArea = new TextArea();
                ChatZone.chatArea.setEditable(false);
                ChatZone.chatArea.setWrapText(true);
        }


        String imgurl = "";
        UsersDetectad = true;
        // Label for text
       
        users = UsersRouteRequests.getUsers(_id,null, null, null);
        System.out.println(users);
        
        
        
        if (users != null) {
            
            // System.out.println("test");
            if((next = users.peek()) != null)
                imgurl =  next.getPictures().get(0).replace('\\', '/');
        }
       
        // Label text = new Label("Image goes here");
        // text.setStyle("-fx-font-size: 24px; -fx-text-fill: red; -fx-font-weight: bold;");

        // Create ImageView to display images (to be fetched via HTTP)
        ImageView imageView = new ImageView();
        imageView.setFitHeight(400);  // Make image area bigger
        imageView.setFitWidth(400);   // Set width of the image
        imageView.setPreserveRatio(true); // Preserve aspect ratio
        if (!imgurl.isEmpty())
            loadImage(imageView, imgurl);

        // System.out.println(imageUrl);
        // Black background div for image (picdiv)
        StackPane picdiv = new StackPane();
        Text name = new Text();
        Text age = new Text();
        Text bio = new Text();
        textStyle(name);
        textStyle(age);
        textStyle(bio);
        VBox userTexts = new VBox(40);
        userTexts.setAlignment(Pos.CENTER);
        userTexts.getChildren().addAll(name, age, bio);
        // picdiv.setBackground(Background.fill(Color.BLACK));
        picdiv.setMinHeight(400);  // Increase the height
        picdiv.getChildren().add(imageView); // Add ImageView to picdiv
        StackPane.setAlignment(imageView, Pos.CENTER_LEFT);
        HBox userDetails = new HBox();
        Button prevPic = new Button("Prev");
        Button nextPic = new Button("Next");
        DisplayUser(name, age, bio, users, imageView);

        prevPic.setOnAction((actionEvent) -> {
           if(UsersDetectad){
            if(Count > 0){
                
                Count--;
            }
            else{
                Count = next.getPictures().size() - 1;
            }
            String url = next.getPictures().get(Count).replace('\\', '/');
            loadImage(imageView, url);
        }
        });
        nextPic.setOnAction((actionEvent) -> {
            if(UsersDetectad){
            
            Count = ++Count % next.getPictures().size();

            String url = next.getPictures().get(Count).replace('\\', '/');
            loadImage(imageView, url);
            }
        });
        prevPic.setAlignment(Pos.BOTTOM_CENTER);
        nextPic.setAlignment(Pos.BOTTOM_CENTER);
        styleOtherButtons(prevPic);
        styleOtherButtons(nextPic);
        userDetails.getChildren().addAll(prevPic, picdiv, nextPic, userTexts);
        
        // Like/Dislike buttons (likeDislikeDiv)
        HBox likeDislikeDiv = new HBox(20);
        Button dislikeButton = new Button("Dislike");
        Button likeButton = new Button("Like");
        styleLikeButton(likeButton);
        styleDislikeButton(dislikeButton);
      
            
        dislikeButton.setOnAction(e -> {
            if(next != null){

                // next.get_id()
                Map<String,String> idMap = new HashMap<>(Map.of(
                    "_id", next.get_id()
                ));
                try {
                    String json = om.writeValueAsString(idMap);  
                    UsersRouteRequests.Dislike(json, _id);
                    
                } catch (Exception ex) {
                    System.out.println("error");
                }
            
                
                users.poll();
                DisplayUser(name, age, bio, users, imageView);
            }
            
        });
        likeButton.setOnAction(e->{
            if(next != null){

                Map<String, String> likeMap = new HashMap<>(Map.of("_id", next.get_id()));
                try {
                   UsersRouteRequests.like(om.writeValueAsString(likeMap), _id);
                   Map<String, Boolean> res =  UsersRouteRequests.CheckMatch(om.writeValueAsString(likeMap), _id);
                   if(res.get("match")){
                       //TODO: match effect                
                        Alert alert = new Alert(AlertType.CONFIRMATION);
                        alert.setTitle("Matched!!!");
                        alert.setHeaderText("");
                        alert.setContentText("check your match box");
    
                        // Show the alert and wait for user response
                        alert.showAndWait();
                   }
                    
                } catch (Exception ex) {
                    System.out.println("error");
                }
                users.poll();
                DisplayUser(name, age, bio, users, imageView);
            }
        });
     
  
        
        likeDislikeDiv.getChildren().addAll(dislikeButton, likeButton);
        likeDislikeDiv.setAlignment(Pos.CENTER);
        likeDislikeDiv.setSpacing(20);
        likeDislikeDiv.setMinHeight(70);
        likeDislikeDiv.setBackground(Background.fill(Color.DARKGRAY));

        // Navigation buttons (Profile, Matches, Messages)
        HBox navBar = new HBox(30);
        Button profileButton = new Button("Profile");
        Button matchesButton = new Button("Matches");
        
        styleOtherButtons(profileButton);
        styleOtherButtons(matchesButton);
       
        profileButton.setOnAction(e -> {
            ProfilePage profilePage = new ProfilePage();
            try {
                profilePage.showProfilePage(stage, _id);
            } catch (URISyntaxException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });
        matchesButton.setOnAction((actionEvent) -> {
            MatchesPage matchesPage = new MatchesPage();
            try {
                matchesPage.showMatchesPage(stage, _id);
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });
        navBar.getChildren().addAll(profileButton, matchesButton);
        navBar.setAlignment(Pos.CENTER);
        navBar.setSpacing(20);
        navBar.setMinHeight(60);
        navBar.setBackground(Background.fill(Color.LIGHTGRAY));

        HBox logout = new HBox(10);
        Button logOutButton = new Button("log out");
        logout.setAlignment(Pos.CENTER);
        styleOtherButtons(logOutButton);
        logOutButton.setOnAction((actionEvent) -> {
            stage.setWidth(500);
            stage.setHeight(600);
            ChatZone.chatArea = null;
            App.isMessagesFetched = false;
            LoginWindow loginWindow = new LoginWindow();
            loginWindow.showLoginWindow(stage,null);
            ChatZone.closeConnection();
        });
        logout.getChildren().add(logOutButton);

        // Main container (VBox)
        VBox maindiv = new VBox(30);
        maindiv.setStyle("-fx-padding: 20px;");
        maindiv.setBackground(Background.fill(Color.WHITE));
        maindiv.setMinHeight(700);  // Set a minimum height for the VBox
        maindiv.getChildren().addAll(userDetails, likeDislikeDiv, navBar, logout);  // Add sections
        
        // Scene
        Scene scene = new Scene(maindiv, 900, 800);  // Adjust window size

        stage.setScene(scene);
        stage.setHeight(800);
        stage.setWidth(900);
        stage.setTitle("Main Page");
        stage.show();
    }
    

    private void DisplayUser(Text name, Text age, Text bio, Queue<User> users, ImageView imageView) {
        if (users != null){
            next = users.peek();
        
            if (next != null) {
                name.setText("Name:\n"+next.getFirstName());
                age.setText("Age:\n" + Integer.toString(next.getAge()));
                bio.setText("bio:\n" + next.getBio());
                // Example: Load an image from a URL (replace with your own URL)
                String imageUrl =  next.getPictures().get(0).replace('\\', '/');
                loadImage(imageView, imageUrl);
                

            } else {
                imageView.setImage(null);
                name.setText("could not find users try again later");
                age.setText(null);
                bio.setText(null);
                UsersDetectad = false;
            }
    }
    else {
        imageView.setImage(null);
        name.setText("could not find users try again later");
        age.setText(null);
        bio.setText(null);
        UsersDetectad = false;
    }
        
    
    }

    //     private Map<String, Object> nextUser(Queue<User> u){
    //     User currentUser = u.peek();
    //     String imgurl = "file:///" + currentUser.getPictures().get(0).replace('\\', '/');
    //     // System.out.println(currentUser);
    //     Map<String, Object> fixedUser = new HashMap<>(Map.of("userpic",imgurl,
    //     "firstName",currentUser.getFirstName(),
    //     "age", currentUser.getAge(),
    //     "bio", currentUser.getBio()
    //     ));
    //     return fixedUser;
    // }
    // Method to load an image from a URL into ImageView
    
    
    private void loadImage(ImageView imageView, String imageUrl) {
        // File file = new File(imageUrl);
        Image image;
        try {
            image = ImageUtils.loadCorrectedImage(imageUrl);
            imageView.setImage(image);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } // Load image from the path

        // Image image;
        


    }
    private void textStyle(Text txt){
        txt.setStyle(
            "-fx-font-family: 'Segoe UI';" + // Clean, modern font
            "-fx-font-size: 18px;" +          // Slightly larger text for name
            "-fx-text-fill: #333333;" +       // Dark text for readability
            "-fx-font-weight: bold;" +        // Bold for emphasis
            "-fx-padding: 10px;"              // Add some space around the text
        );
    }
    // Style for Like/Dislike buttons with hover effects
    private void styleLikeButton(Button button) {
        button.setStyle("-fx-font-size: 16px; -fx-padding: 10px 20px; -fx-background-radius: 20px;");
        button.setOnMouseEntered((MouseEvent e) -> button.setStyle("-fx-font-size: 16px; -fx-padding: 10px 20px; -fx-background-radius: 20px; -fx-background-color: #4CAF50;"));
        button.setOnMouseExited((MouseEvent e) -> button.setStyle("-fx-font-size: 16px; -fx-padding: 10px 20px; -fx-background-radius: 20px;;"));
    }
    private void styleDislikeButton(Button button) {
        button.setStyle("-fx-font-size: 16px; -fx-padding: 10px 20px; -fx-background-radius: 20px;");
        button.setOnMouseEntered((MouseEvent e) -> button.setStyle("-fx-font-size: 16px; -fx-padding: 10px 20px; -fx-background-radius: 20px; -fx-background-color:rgb(178, 12, 34);"));
        button.setOnMouseExited((MouseEvent e) -> button.setStyle("-fx-font-size: 16px; -fx-padding: 10px 20px; -fx-background-radius: 20px;;"));
    }
    private void styleOtherButtons(Button button) {
        button.setStyle("-fx-font-size: 16px; -fx-padding: 10px 20px; -fx-background-radius: 20px;");
        button.setOnMouseEntered((MouseEvent e) -> button.setStyle("-fx-font-size: 16px; -fx-padding: 10px 20px; -fx-background-radius: 20px; -fx-background-color:rgb(192, 4, 195);"));
        button.setOnMouseExited((MouseEvent e) -> button.setStyle("-fx-font-size: 16px; -fx-padding: 10px 20px; -fx-background-radius: 20px;;"));
    }

    // Style for Navigation buttons
//     private void styleNavButton(Button button) {
//         button.setStyle("-fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-radius: 15px;");
//         button.setOnMouseEntered((MouseEvent e) -> button.setStyle("-fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-radius: 15px; -fx-background-color: #2196F3;"));
//         button.setOnMouseExited((MouseEvent e) -> button.setStyle("-fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-radius: 15px;"));
//     }
}
