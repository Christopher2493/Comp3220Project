// AI-assisted (Claude): temporary appointments test screen controller (book, list, cancel) using ClinicRepository. Reviewed by Vince.
package group2;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * Controller for appointments.fxml: books appointments, lists them, and cancels them.
 *
 * Works the same way as the other screen controllers:
 * - @FXML fields are linked to the elements in appointments.fxml with the same fx:id.
 * - Button clicks call the @FXML method named in the button's onAction.
 * - All data and all booking rules live in ClinicRepository; this class only reads the
 *   form, calls the repository, and shows the result.
 */
public class AppointmentsController {

    // UI elements from appointments.fxml, matched by fx:id
    @FXML private ComboBox<Patient> patientBox;          // drop-down to choose the patient
    @FXML private ComboBox<Doctor> doctorBox;            // drop-down to choose the doctor
    @FXML private TextField dateField;                   // date input (free text for now)
    @FXML private TextField timeField;                   // time input (free text for now)
    @FXML private ListView<Appointment> appointmentList; // all booked appointments
    @FXML private Label statusLabel;                     // success / error messages for the user
    private final ClinicRepository repository = App.getRepository(); // shared data access, created in App

    /**
     * Called automatically by JavaFX after appointments.fxml is loaded.
     * Fills both drop-downs and the appointment list from the database.
     */
    @FXML
    private void initialize() {
        patientBox.getItems().setAll(repository.getAllPatients());
        doctorBox.getItems().setAll(repository.getAllDoctors());
        refresh();
    }

    /** Reloads the appointment list from the database. */
    private void refresh() {
        appointmentList.getItems().setAll(repository.getAllAppointments());
    }

    /**
     * "Book" button: takes the chosen patient and doctor plus the typed date and
     * time, and asks the repository to book. The repository enforces the rules
     * (patient/doctor exist, one appointment per patient, no double-booked slot)
     * and its message is shown if the booking is refused.
     */
    @FXML
    private void book() {
        Patient patient = patientBox.getValue();
        Doctor doctor = doctorBox.getValue();
        if (patient == null || doctor == null) {
            statusLabel.setText("Choose a patient and a doctor.");
            return;
        }
        try {
            repository.bookAppointment(patient.getPatientId(), doctor.getDocId(), dateField.getText(), timeField.getText());
            statusLabel.setText("Booked " + patient.getPatientName() + " with " + doctor.getDocName() + ".");
            dateField.clear();
            timeField.clear();
            refresh();
        } catch (IllegalArgumentException e) {
            statusLabel.setText(e.getMessage());
        }
    }

    /**
     * "Cancel Selected Appointment" button: cancels the appointment selected in the list.
     * Appointments are identified by patient ID, because each patient has at most one.
     */
    @FXML
    private void cancel() {
        Appointment selected = appointmentList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Select an appointment to cancel.");
            return;
        }
        try {
            repository.cancelAppointment(selected.getPatientId());
            statusLabel.setText("Appointment cancelled.");
            refresh();
        } catch (IllegalArgumentException e) {
            statusLabel.setText(e.getMessage());
        }
    }

    /** "Back" button: returns to the main menu. */
    @FXML
    private void back() throws IOException {
        App.setRoot("primary");
    }
}
