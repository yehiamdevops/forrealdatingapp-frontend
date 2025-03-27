package forrealdatingapp;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ProfilePage {
    private static User user;
    private static final ObjectMapper om = new ObjectMapper();
    private static final String PRIMARY_COLOR = "#2196F3";
    private static final String SECONDARY_COLOR = "#FFF";
    private static final String BACKGROUND_COLOR = "#F5F5F5";
    
    private final SimpleStringProperty name = new SimpleStringProperty();
    private final SimpleIntegerProperty age = new SimpleIntegerProperty();
    private final SimpleStringProperty bio = new SimpleStringProperty();
    
    private final List<ImageView> pictureViews = new ArrayList<>();
    private final GridPane pictureGrid = new GridPane();
    private final VBox detailsSection = new VBox(15);
    
    private final Label notificationLabel = new Label();
    private final PauseTransition notificationTimeout = new PauseTransition(Duration.seconds(3));
    private String _id;
    public void showProfilePage(Stage stage, String _id) throws URISyntaxException {
        user = UsersRouteRequests.getMyProfile(_id);
        this._id = _id;
        // Initialize example data
        name.set(user.getFirstName());
        age.set(user.getAge());
        bio.set(user.getBio());
        List<String> userPictures = user.getPictures(); // Assumes pictures are stored as Strings (e.g., URLs or file paths)
        System.out.println(userPictures);
        initializePictureGrid(userPictures);

        VBox root = createStyledRoot();
        setupNotificationLabel();
        
        VBox header = createProfileHeader();
        VBox pictureSection = createPictureSection();
        VBox details = createDetailsSection(stage,_id);
        
        root.getChildren().addAll(notificationLabel, header, pictureSection, details);
        ScrollPane scrollPane = new ScrollPane(root);  // Make the VBox scrollable
        scrollPane.setFitToWidth(true);  // Make content fit to ScrollPane width
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        Scene scene = new Scene(scrollPane, 800, 900);
        stage.setTitle("Profile");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createStyledRoot() {
        VBox root = new VBox(30);
        root.setStyle(String.format("-fx-background-color: %s;", BACKGROUND_COLOR));
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);
        return root;
    }

    private VBox createProfileHeader() throws URISyntaxException {
        VBox header = new VBox(15);
        header.setAlignment(Pos.CENTER);
        
        // Create a default avatar placeholder using shapes
  
        
        StackPane avatarPlaceholder = createAvatarPlaceholder();
        
        // Edit profile picture button
        Button editProfilePic = createStyledButton("Change Profile Picture", "photo");
        Button removeProfilePic = createStyledButton("Remove Profile Picture", "photo");
        removeProfilePic.setOnAction((e) -> {
         avatarPlaceholder.getChildren().clear();
         avatarPlaceholder.getChildren().add(createAvatarPlaceholder());
            


        });
        editProfilePic.setOnAction(e -> handleProfilePictureChange(avatarPlaceholder));
        if (user.getProfilePicture() != null) {
            user.getProfilePicture();
            Image image;
            try {
                image = ImageUtils.loadCorrectedImage(user.getProfilePicture());
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(120);
                imageView.setFitWidth(120);
                
                // Create circular clip for the image
                Circle clip = new Circle(60,60,60);
                imageView.setClip(clip);
                // imageView.setPreserveRatio(true);
                // Replace placeholder with new image
                avatarPlaceholder.getChildren().clear();
                avatarPlaceholder.getChildren().add(imageView);
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            
        }
        header.getChildren().addAll(avatarPlaceholder, editProfilePic, removeProfilePic);
        return header;
    }

    private StackPane createAvatarPlaceholder() {
        StackPane avatarPlaceholder = new StackPane();
        
        // Create circular background
        Circle circle = new Circle(60);
        circle.setFill(Color.web(PRIMARY_COLOR));
        
        // Create initials label
        Label initials = new Label(getInitials(name.get()));
        initials.setStyle("""
            -fx-text-fill: white;
            -fx-font-size: 36px;
            -fx-font-weight: bold;
            """);
        
        avatarPlaceholder.getChildren().addAll(circle, initials);
        avatarPlaceholder.setEffect(new DropShadow(10, Color.gray(0.5)));
        return avatarPlaceholder;
    }

    private String getInitials(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) return "?";
        
        String[] names = fullName.split(" ");
        StringBuilder initials = new StringBuilder();
        
        for (String name : names) {
            if (!name.isEmpty()) {
                initials.append(name.charAt(0));
            }
        }
        
        return initials.toString().toUpperCase();
    }

    private void handleProfilePictureChange(StackPane avatarContainer) {
        FileChooser fileChooser = createImageFileChooser();
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            System.out.println(file.toString());
            try {
                String urlToDB = CloudinaryUtils.Upload(file);
                Image image = ImageUtils.loadCorrectedImage(urlToDB);
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(120);
                imageView.setFitWidth(120);
                
                // Create circular clip for the image
                Circle clip = new Circle(60,60,60);
                imageView.setClip(clip);
                imageView.setPreserveRatio(true);
                // Replace placeholder with new image
                Map<String, String> jsonMap = new HashMap<>();
                
                jsonMap.put("url", urlToDB);
                boolean success = UsersRouteRequests.updateProfilePicture(_id, jsonMap);
                if(success){

                    avatarContainer.getChildren().clear();
                    avatarContainer.getChildren().add(imageView);
                    showSuccess("Profile picture updated successfully");
                }
                else{

                    showError("Failed to update image");
                }
           
            } catch (Exception e) {
                showError("Failed to load image: " + e.getMessage());
            }
        }
    }

    private VBox createPictureSection() {
        VBox pictureSection = new VBox(15);
        pictureSection.setAlignment(Pos.CENTER);
        
        pictureGrid.setHgap(15);
        pictureGrid.setVgap(15);
        pictureGrid.setAlignment(Pos.CENTER);
        
        Button addPictureButton = createStyledButton("Add Picture", "add");
        addPictureButton.setOnAction(e -> handleAddPicture());
        
        Label sectionTitle = new Label("My Pictures");
        sectionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        pictureSection.getChildren().addAll(sectionTitle, pictureGrid, addPictureButton);
        return pictureSection;
    }

    private VBox createDetailsSection(Stage stage, String _id) {
        TextField nameField = createStyledTextField("Name", name.get());
        nameField.textProperty().bindBidirectional(name);
        
        TextField ageField = createStyledTextField("Age", String.valueOf(age.get()));
        ageField.textProperty().addListener((obs, old, newValue) -> {
            if (newValue.matches("\\d*")) {
                age.set(newValue.isEmpty() ? 0 : Integer.parseInt(newValue));
            }
        });
        
        TextArea bioArea = new TextArea(bio.get());
        bioArea.setWrapText(true);
        bioArea.textProperty().bindBidirectional(bio);
        bioArea.setStyle(getTextFieldStyle());
        bioArea.setPrefRowCount(3);
        
        Button saveButton = createStyledButton("Save Profile", "save");
        Button backToProfileButton = createStyledButton("back to profile", "save");
        saveButton.setOnAction(e -> handleSaveProfile());
        backToProfileButton.setOnAction((actionEvent) -> {
            MainPage mp = new MainPage();
            mp.showMainPage(stage, _id);
        });
        
        Label sectionTitle = new Label("Profile Details");
        sectionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        detailsSection.getChildren().addAll(
            sectionTitle,
            new Label("Name"),
            nameField,
            new Label("Age"),
            ageField,
            new Label("Bio"),
            bioArea,
            saveButton,
            backToProfileButton
        );
        
        return detailsSection;
    }

    // ... (rest of the methods remain the same)

    private void setupNotificationLabel() {
        notificationLabel.setVisible(false);
        notificationLabel.setStyle("""
            -fx-background-color: #333333;
            -fx-text-fill: white;
            -fx-padding: 10;
            -fx-background-radius: 5;
            """);
        notificationLabel.setMaxWidth(Double.MAX_VALUE);
        notificationLabel.setAlignment(Pos.CENTER);
        
        notificationTimeout.setOnFinished(e -> notificationLabel.setVisible(false));
    }

    private void showNotification(String message, String type) {
        notificationLabel.setText(message);
        
        String backgroundColor = switch(type) {
            case "error" -> "#ff4444";
            case "success" -> "#4CAF50";
            default -> "#333333";
        };
        
        notificationLabel.setStyle(String.format("""
            -fx-background-color: %s;
            -fx-text-fill: white;
            -fx-padding: 10;
            -fx-background-radius: 5;
            """, backgroundColor));
            
        notificationLabel.setVisible(true);
        notificationTimeout.playFromStart();
    }

    private void showSuccess(String message) {
        showNotification(message, "success");
    }

    private void showError(String message) {
        showNotification(message, "error");
    }

    private TextField createStyledTextField(String prompt, String initialValue) {
        TextField field = new TextField(initialValue);
        field.setPromptText(prompt);
        field.setStyle(getTextFieldStyle());
        return field;
    }

    public Button createStyledButton(String text, String icon) {
        Button button = new Button(text);
        button.setStyle(String.format("""
            -fx-background-color: %s;
            -fx-text-fill: %s;
            -fx-padding: 10 20;
            -fx-background-radius: 5;
            -fx-cursor: hand;
            """, PRIMARY_COLOR, SECONDARY_COLOR));
        
        button.setOnMouseEntered(e -> 
            button.setEffect(new DropShadow(10, Color.web(PRIMARY_COLOR))));
        button.setOnMouseExited(e -> 
            button.setEffect(null));
            
        return button;
    }

    private String getTextFieldStyle() {
        return """
            -fx-background-color: white;
            -fx-padding: 10;
            -fx-background-radius: 5;
            -fx-border-radius: 5;
            -fx-border-color: #E0E0E0;
            -fx-border-width: 1;
            """;
    }

    private FileChooser createImageFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        return fileChooser;
    }

    private void handleAddPicture() {
        FileChooser fileChooser = createImageFileChooser();
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                addPictureToGrid(file);
                //add also to db here
                Map<String, String> jsonMap = new HashMap<>();
                String urlToDB = CloudinaryUtils.Upload(file);
                jsonMap.put("url",urlToDB);
                // handle a cloud entry and create a url here for now file for system path
                String json = om.writeValueAsString(jsonMap);
                UsersRouteRequests.addPicture(json, user.get_id());
                showSuccess("Picture added successfully");
            } catch (Exception e) {
                showError("Failed to add picture: " + e.getMessage());
            }
        }
    }
    
    private void addPictureToGrid(File file) {
        // Create image view
        Image image = new Image(file.toURI().toString());
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(150);
        imageView.setFitWidth(150);
        
        // Create container for the image and buttons
        VBox pictureContainer = new VBox(10);
        pictureContainer.setAlignment(Pos.CENTER);
        pictureContainer.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-background-radius: 5;");
        
        // Add hover effect
        pictureContainer.setOnMouseEntered(e -> 
            pictureContainer.setEffect(new DropShadow(10, Color.web(PRIMARY_COLOR))));
        pictureContainer.setOnMouseExited(e -> 
            pictureContainer.setEffect(null));
        
        // Create buttons
        HBox buttonContainer = new HBox(10);
        buttonContainer.setAlignment(Pos.CENTER);
        
        Button editButton = createStyledButton("Edit", "edit");
        editButton.setOnAction(e -> handleEditPicture(imageView));
        
        Button removeButton = createStyledButton("Remove", "remove");
        removeButton.setOnAction(e -> handleRemovePicture(pictureContainer));
        
        buttonContainer.getChildren().addAll(editButton, removeButton);
        pictureContainer.getChildren().addAll(imageView, buttonContainer);
        
        // Add to grid
        int column = pictureViews.size() % 3;
        int row = pictureViews.size() / 3;
        pictureGrid.add(pictureContainer, column, row);
        pictureViews.add(imageView);
    }
    
    private void handleEditPicture(ImageView imageView) {
        FileChooser fileChooser = createImageFileChooser();
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                Image newImage = new Image(file.toURI().toString());
                imageView.setImage(newImage);
                showSuccess("Picture updated successfully");
            } catch (Exception e) {
                showError("Failed to update picture: " + e.getMessage());
            }
        }
    }
    
    private void handleRemovePicture(VBox pictureContainer) {
        pictureGrid.getChildren().remove(pictureContainer);
        
        // Find and remove the ImageView from our list
        pictureContainer.getChildren().stream()
            .filter(node -> node instanceof ImageView)
            .map(node -> (ImageView) node)
            .findFirst()
            .ifPresent(pictureViews::remove);
        
        // Reorganize the grid
        reorganizePictureGrid();
        showSuccess("Picture removed successfully");
    }
    
    private void reorganizePictureGrid() {
        // Clear the grid
        pictureGrid.getChildren().clear();
        
        // Re-add all pictures in order
        for (int i = 0; i < pictureViews.size(); i++) {
            VBox container = (VBox) pictureViews.get(i).getParent();
            pictureGrid.add(container, i % 3, i / 3);
        }
    }
    
    private void handleSaveProfile() {
        if (name.get().isEmpty() || age.get() <= 0 || bio.get().isEmpty()) {
            showError("Please fill in all fields correctly");
            return;
        }
    
        // Here you would typically save to a database or make an API call
        System.out.println("Saving profile:");
        System.out.println("Name: " + name.get());
        System.out.println("Age: " + age.get());
        System.out.println("Bio: " + bio.get());
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("firstName", name.get());
        jsonMap.put("age", age.get());
        jsonMap.put("bio", bio.get());
        try {
            String json = om.writeValueAsString(jsonMap);    
            UsersRouteRequests.updateProfile(json, user.get_id());    
            
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        
        showSuccess("Profile saved successfully");
    }
    private void initializePictureGrid(List<String> picturePaths) {
        pictureGrid.getChildren().clear(); // Clear existing grid content
        if (picturePaths != null && !picturePaths.isEmpty()) {
            // System.out.println("test pic paths: \n"+picturePaths);
            int col = 0, row = 0;
            for (String picturePath : picturePaths) {
                try {
                    
                    // Image image = ImageUtils.loadCorrectedImage(file); // Load image from the path
                    Image img = ImageUtils.loadCorrectedImage(picturePath);
                    ImageView imageView = new ImageView();
                    imageView.setImage(img);
                    imageView.setFitHeight(150);
                    imageView.setFitWidth(150);
                    imageView.setPreserveRatio(true);
                    // imageView.setRotate(90);
                    pictureViews.add(imageView); // Add ImageView to the list for later use

                    VBox pictureContainer = new VBox(10, imageView);
                    pictureContainer.setAlignment(Pos.CENTER);
                    pictureContainer.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-background-radius: 5;");
    
                    pictureGrid.add(pictureContainer, col, row);
    
                    // Move to next row after 3 columns
                    col++;
                    if (col == 3) {
                        col = 0;
                        row++;
                    }
                } catch (Exception e) {
                    showError("Failed to load picture: " + e.getMessage());
                }
            }
        }
    }

    
}