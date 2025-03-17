package hellofx;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Pagination;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MatchesPage {
    private static ChatZone chatZone;
    private static ListView<HBox> matchesListView;
    private static Map<String, HBox> matchBoxMap = new HashMap<>();

    void showMatchesPage(Stage stage,String _id) throws IOException {
        // Create a back button to return to the main page
        Button backToProfileButton = new Button("Back to Main Screen");
        backToProfileButton.setOnAction((actionEvent) -> {
            MainPage mainPage = new MainPage();
            mainPage.showMainPage(stage,_id);
        });

        // Create a list of matches (for demonstration purposes)
        
        // Create a ListView to display matches
        Pagination pagination = new Pagination();
        pagination.setStyle("-fx-page-information-visible: false;");
        pagination.setMaxPageIndicatorCount(1);
        pagination.setPageFactory((pageIndex)->{
            System.out.println(pageIndex);
            List<Match> matches = getMatches(_id, pageIndex + 1);
            
            if (!matches.isEmpty()){
                matchesListView = new ListView<>();
                for (Match match : matches) {
                    HBox matchBox;
                    try {
                        matchBox = createMatchBox(match, stage,_id);
                        matchesListView.getItems().add(matchBox);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (URISyntaxException ex) {
                        ex.printStackTrace();
                    }
            
                    
                }
            }
            else{
                pagination.setCurrentPageIndex(pageIndex);
                return new Label("no more matches!!!");
            }
        
            return matchesListView;

        });
        ScrollPane sp = new ScrollPane();
        sp.setContent(matchesListView);
        // Main layout
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.getChildren().addAll(backToProfileButton, pagination);

        // Scene
        Scene scene = new Scene(mainLayout, 900, 800);
        stage.setScene(scene);
        stage.setTitle("Matches");
        stage.show();
    }

    // Helper method to create a match box (HBox) for each match
private HBox createMatchBox(Match match,Stage stage,String _id) throws IOException, URISyntaxException {
    // Create an ImageView for the profile picture
    Node imageOrLabel;
    ImageView profilePicture = new ImageView();
    profilePicture.setFitWidth(50);  // Set the width of the image
    profilePicture.setFitHeight(50); // Set the height of the image
    profilePicture.setPreserveRatio(true); // Preserve the aspect ratio
    profilePicture.setStyle("-fx-border-radius: 25px; -fx-border-color: #cccccc; -fx-border-width: 2px;"); // Optional: Add a circular border
    // Load the profile picture (replace with the actual image URL or path)
    String imageUrl = match.getProfilePictureUrl(); // Assuming Match class has a method to get the image URL
    if (imageUrl != null && !imageUrl.isEmpty()) {
        // File file = new File(imageUrl);
        Image image = ImageUtils.loadCorrectedImage(imageUrl);
        profilePicture.setImage(image);
        imageOrLabel = profilePicture;
    } else {
        // Set a default image if no URL is provided
        imageOrLabel = new Label("no profile pic");
    }

    // Match name
    Label nameLabel = new Label(match.getName());
    nameLabel.setFont(Font.font(18));
    nameLabel.setTextFill(Color.DARKBLUE);

    // Last message
    Label lastMessageLabel = new Label(match.getLastMessage());
    lastMessageLabel.setFont(Font.font(14));
    lastMessageLabel.setTextFill(Color.GRAY);

    // Layout for match details
    HBox matchBox = new HBox(10);
    matchBoxMap.put(match._id, matchBox);
    VBox matchDetails = new VBox(5);
    matchDetails.getChildren().addAll(nameLabel, lastMessageLabel);
    Button unmatchButton = new Button("Unmatch");
    unmatchButton.setTextFill(Color.RED);
    unmatchButton.setStyle("-fx-background-color: transparent; -fx-border-color: red;");
    unmatchButton.setOnAction(e -> {
        System.out.println("test-button-unmatch");
        
        boolean unmatched = UsersRouteRequests.Unmatch( _id, match._id);
        if(unmatched){
            //progress: socket logic done, database logic missing
            matchesListView.getItems().remove(matchBox);
            ChatZone.writer.println("UnmatchSocket| match:" + match._id);
            ChatZone.chatArea = null;
        }


        
});

// Spacer to push the button to the right
Button showProfile = new Button("Show Profile");
showProfile.setTextFill(Color.BLUEVIOLET);
showProfile.setStyle("-fx-background-color: transparent; -fx-border-color: blue;");
showProfile.setOnAction(e -> {
    MatchedProfilePage mpp = new MatchedProfilePage();
    try {
        mpp.showMatchedProfilePage(stage, _id, match._id);
    } catch (URISyntaxException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
    }

});

Region spacer = new Region();
HBox.setHgrow(spacer, Priority.ALWAYS);

    // Container for the match
    matchBox.setAlignment(Pos.CENTER_LEFT);
    matchBox.setPadding(new Insets(10));
    matchBox.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1px; -fx-border-radius: 5px;");
    matchBox.getChildren().addAll(imageOrLabel, matchDetails,spacer, showProfile,unmatchButton); // Add the profile picture and match details
    matchBox.setOnMouseClicked((mouseEvent) -> {
        System.out.println(match.getId());
        if (chatZone == null)
            chatZone = new ChatZone();
        chatZone.showChatZone(stage, match.getId(),_id);
        /*here i need guidance cause in the chat zone stage there a socket talk 
         */
        
    });
    return matchBox;
}

    // Mock data for matches (replace with real data from your app)
    private List<Match> getMatches(String _id, int index) {
        List<Match> matches = new ArrayList<>();
        List<User> matchesList = UsersRouteRequests.getMatches(Integer.toString(index) , _id);
        if (matchesList != null ){
            for (User elem : matchesList) {
                matches.add(new Match(elem.get_id(),elem.getFirstName(), elem.getBio(), elem.getProfilePicture()));
                
            }
            
        }
   
        return matches;
    }

    // Inner class to represent a match
    private static class Match {
        private final String _id;
        private final String name;
        private final String lastMessage;
        private final String profilePictureUrl; // Add this field
    
        public Match(String _id,String name, String lastMessage, String profilePictureUrl) {
            this._id = _id;
            this.name = name;
            this.lastMessage = lastMessage;
            this.profilePictureUrl = profilePictureUrl;
        }
    
        public String getId() {
            return _id;
        }
        public String getName() {
            return name;
        }
    
        public String getLastMessage() {
            return lastMessage;
        }
    
        public String getProfilePictureUrl() {
            return profilePictureUrl;
        }
    }
    static void cleanCurrentUnmatch(String id){
        // System.out.println("cleanCurrentUnmatch" + matchBoxMap.get(id));
        Platform.runLater(()->{
            matchesListView.getItems().remove(matchBoxMap.get(id));
        });
        
    }
}
