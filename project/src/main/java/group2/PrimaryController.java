package group2;

import java.io.IOException;

import javafx.fxml.FXML;

@SuppressWarnings("unused")
public class PrimaryController {

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
    @FXML
    private void exit() throws IOException {
        App.exit();
    }
}
