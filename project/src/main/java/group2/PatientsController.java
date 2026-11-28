// AI-assisted (Claude): temporary patients test screen controller (list, add) using ClinicRepository. Reviewed by Vince.
package group2;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * Controller for patients.fxml: lists patients and registers new ones.
 *
 * Works the same way as DoctorsController:
 * - @FXML fields are linked to the elements in patients.fxml with the same fx:id.
 * - Button clicks call the @FXML method named in the button's onAction.
 * - All data comes from the shared ClinicRepository.
 */
public class PatientsController {

    // UI elements from patients.fxml, matched by fx:id
    @FXML private ListView<Patient> patientList; // list of all patients (shows Patient.toString())
    @FXML private TextField idField;             // "Patient ID" input
    @FXML private TextField nameField;           // "Patient name" input
    @FXML private Label statusLabel;             // success / error messages for the user
    private final ClinicRepository repository = App.getRepository(); // shared data access, created in App

    /** Called automatically by JavaFX after patients.fxml is loaded; fills the list. */
    @FXML
    private void initialize() {
        refresh();
    }

    /** Reloads the patient list from the database. */
    private void refresh() {
        patientList.getItems().setAll(repository.getAllPatients());
    }

    /**
     * "Add Patient" button: reads the two inputs, asks the repository to save the
     * patient, then clears the form and refreshes the list.
     * Any problem is shown in the status label instead of crashing the app.
     */
    @FXML
    private void addPatient() {
        try {
            int id = Integer.parseInt(idField.getText().trim()); // fails if the ID is not a number
            repository.addPatient(id, nameField.getText());
            statusLabel.setText("Patient " + id + " added.");
            idField.clear();
            nameField.clear();
            refresh();
        } catch (NumberFormatException e) {
            statusLabel.setText("Patient ID must be a whole number.");
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
