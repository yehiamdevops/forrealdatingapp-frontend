package hellofx;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.Pagination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class PaginationExample extends Application {

    private static final int ITEMS_PER_PAGE = 10; // Max items per page
    private List<String> clientData; // Holds client-specific data

    @Override
    public void start(Stage primaryStage) {
        // Simulated dynamic client data (this could be fetched from a database or API)
        clientData = getClientData();

        // Calculate number of pages dynamically
        int totalPages = (int) Math.ceil((double) clientData.size() / ITEMS_PER_PAGE);
        if (totalPages == 0) totalPages = 1; // Ensure at least 1 page exists

        // Create Pagination
        Pagination pagination = new Pagination(totalPages, 0);
        pagination.setStyle("-fx-page-information-visible: false;");
        pagination.setMaxPageIndicatorCount(1);
        
        
        // Set the page factory to create pages dynamically
        // this method expect to recieve javafx node
        pagination.setPageFactory(this::createPage);

        // Layout
        StackPane root = new StackPane(pagination);
        Scene scene = new Scene(root, 400, 300);

        // Set Stage
        primaryStage.setTitle("Dynamic JavaFX Pagination");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Create dynamic ListView per page
    private ListView<HBox> createPage(int pageIndex) {
        System.out.println("test-factory");
        ListView<HBox> listView = new ListView<>();
        
        int start = pageIndex * ITEMS_PER_PAGE;//10
        int end = Math.min(start + ITEMS_PER_PAGE, clientData.size());//20, 4 = 4

        for (int i = start; i < end; i++) {
            HBox hbox = new HBox(new Text(clientData.get(i)));
            hbox.setSpacing(10);
            listView.getItems().add(hbox);
        }

        return listView;
    }

    // Simulated client data (this could be dynamically fetched)
    private List<String> getClientData() {
        List<String> data = new ArrayList<>();
        
        // Simulating a client with a random number of items (e.g., 7 items instead of 10)
        data.add("Item 1");
        data.add("Item 2");
        data.add("Item 3");
        data.add("Item 4");




        return data;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
