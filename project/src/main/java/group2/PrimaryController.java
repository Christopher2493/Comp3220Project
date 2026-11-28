// AI-assisted (Claude): temporary main menu routing to the test screens. Reviewed by Vince.
package group2;

import java.io.IOException;

import javafx.fxml.FXML;

/**
 * Controller for primary.fxml, the main menu shown when the app starts.
 *
 * Each button in primary.fxml has onAction="#methodName", which makes JavaFX call
 * the matching @FXML method below when it is clicked. Switching screens is done with
 * App.setRoot("name"), which loads name.fxml into the window.
 *
 * This is a temporary test menu; Ayushmaan's GUI design will replace it.
 */
@SuppressWarnings("unused") // the methods are called from FXML, so the compiler sees them as unused
public class PrimaryController {

    /** "Doctors" button: opens doctors.fxml. */
    @FXML
    private void openDoctors() throws IOException {
        App.setRoot("doctors");
    }

    /** "Patients" button: opens patients.fxml. */
    @FXML
    private void openPatients() throws IOException {
        App.setRoot("patients");
    }

    /** "Appointments" button: opens appointments.fxml. */
    @FXML
    private void openAppointments() throws IOException {
        App.setRoot("appointments");
    }

    /** Opens the original template screen (secondary.fxml); no button uses it right now. */
    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }

    /** "Exit" button: closes the application. */
    @FXML
    private void exit() throws IOException {
        App.exit();
    }
}
