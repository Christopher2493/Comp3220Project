// AI-assisted (Claude): Patient model, mirrors the patients table (PatientId, PatientName). Reviewed by Vince.
package group2;

/**
 * A patient registered at the clinic.
 *
 * Maps one row of the {@code patients} table: PatientId -> patientId, PatientName -> patientName.
 * Objects are created by {@link ClinicRepository} when it reads the database,
 * and are shown in the GUI lists (the list uses {@link #toString()}).
 *
 * The fields are final: a Patient object is a read-only snapshot of a database row.
 */
public class Patient {

    private final int patientId;      // PatientId column, unique per patient
    private final String patientName; // PatientName column

    /**
     * Creates a patient from the values of one {@code patients} row.
     *
     * @param patientId   the patient's unique ID
     * @param patientName the patient's name
     */
    public Patient(int patientId, String patientName) {
        this.patientId = patientId;
        this.patientName = patientName;
    }

    /** Returns the patient's unique ID (PatientId column). */
    public int getPatientId() {
        return patientId;
    }

    /** Returns the patient's name (PatientName column). */
    public String getPatientName() {
        return patientName;
    }

    /**
     * Text shown for this patient in JavaFX lists and drop-downs,
     * e.g. "1082 - Jane Doe".
     */
    @Override
    public String toString() {
        return patientId + " - " + patientName;
    }
}
