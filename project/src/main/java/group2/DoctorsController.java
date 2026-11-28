// AI-assisted (Claude): temporary doctors test screen controller (list, schedule, add) using ClinicRepository. Reviewed by Vince.
package group2;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Controller for doctors.fxml: lists doctors, shows the selected doctor's
 * schedule, and adds new doctors.
 *
 * How the pieces connect:
 * - Each @FXML field is filled in by JavaFX from the element in doctors.fxml
 *   with the same fx:id (e.g. fx:id="doctorList" -> doctorList).
 * - Button clicks call the @FXML method named in the button's onAction.
 * - All data comes from the shared ClinicRepository; this class never touches SQL.
 */
public class DoctorsController {

    // UI elements from doctors.fxml, matched by fx:id
    @FXML private ListView<Doctor> doctorList;  // list of all doctors (shows Doctor.toString())
    @FXML private TextArea scheduleArea;        // read-only box showing the selected doctor's schedule
    @FXML private TextField idField;            // "Doctor ID" input
    @FXML private TextField nameField;          // "Doctor name" input
    @FXML private Label statusLabel;            // success / error messages for the user
    private final ClinicRepository repository = App.getRepository(); // shared data access, created in App

    /**
     * Called automatically by JavaFX after doctors.fxml is loaded and the
     * @FXML fields above are filled in. Sets up the screen.
     */
    @FXML
    private void initialize() {
        // Whenever the selected doctor in the list changes, show that doctor's schedule.
        doctorList.getSelectionModel().selectedItemProperty().addListener((obs, old, doctor) -> showSchedule(doctor));
        refresh();
    }

    /** Reloads the doctor list from the database. */
    private void refresh() {
        doctorList.getItems().setAll(repository.getAllDoctors());
    }

    /**
     * Fills the schedule box for the given doctor.
     *
     * @param doctor the selected doctor, or null when the selection is cleared
     */
    private void showSchedule(Doctor doctor) {
        if (doctor == null) {
            scheduleArea.clear();
            return;
        }
        Schedule schedule = repository.getSchedule(doctor.getDocId());
        scheduleArea.setText(schedule == null ? "No schedule set for " + doctor.getDocName() : schedule.toString());
    }

    /**
     * "Add Doctor" button: reads the two inputs, asks the repository to save the
     * doctor, then clears the form and refreshes the list.
     * Any problem is shown in the status label instead of crashing the app.
     */
    @FXML
    private void addDoctor() {
        try {
            int id = Integer.parseInt(idField.getText().trim()); // fails if the ID is not a number
            repository.addDoctor(id, nameField.getText());
            statusLabel.setText("Doctor " + id + " added.");
            idField.clear();
            nameField.clear();
            refresh();
        } catch (NumberFormatException e) {
            statusLabel.setText("Doctor ID must be a whole number.");
        } catch (IllegalArgumentException e) {
            // Thrown by ClinicRepository with a readable reason (blank name, duplicate ID, ...).
            statusLabel.setText(e.getMessage());
        }
    }

    /** "Back" button: returns to the main menu. */
    @FXML
    private void back() throws IOException {
        App.setRoot("primary");
    }
}
