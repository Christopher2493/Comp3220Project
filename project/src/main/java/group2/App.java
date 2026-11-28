package group2;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    

    private static Scene scene;
    private static ClinicRepository repository; // AI-assisted (Claude): one shared repository for all controllers. Reviewed by Vince.

    @Override
    public void start(Stage stage) throws IOException {
        // Create the repository BEFORE loading the first screen: each controller reads
        // App.getRepository() when FXML creates it, so it must already exist.
        // This also creates the database tables if the Data file is new.
        repository = new ClinicRepository();
        scene = new Scene(loadFXML("primary"), 640, 480);
        
        stage.setScene(scene);
        stage.show();

    }

    /** Switches the window to another screen, e.g. setRoot("doctors") loads doctors.fxml. */
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }
    /** Gives controllers the shared ClinicRepository (the one way screens reach the data). */
    static ClinicRepository getRepository() {
        return repository;
    }
    static void exit() {
        System.exit(0);
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}