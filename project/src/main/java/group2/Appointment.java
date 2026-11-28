// AI-assisted (Claude): Appointment model, mirrors the appointments table (PatientId, DocId, Date, Time). Reviewed by Vince.
package group2;

/**
 * A booking of one patient with one doctor at a date and time.
 *
 * Maps one row of the {@code appointments} table. It stores IDs rather than
 * Patient/Doctor objects, exactly like the table does; use the IDs to look the
 * patient or doctor up through {@link ClinicRepository}.
 *
 * Date and time are free text, as stored in the database (e.g. "2026-12-01", "09:00").
 * Note: in the current table PatientId is the primary key, so each patient can
 * have only one appointment at a time.
 */
public class Appointment {

    private final int patientId; // PatientId column: who is booked
    private final int docId;     // DocId column: which doctor they see
    private final String date;   // Date column (free text)
    private final String time;   // Time column (free text)

    /**
     * Creates an appointment from the values of one {@code appointments} row.
     *
     * @param patientId the booked patient's ID
     * @param docId     the doctor's ID
     * @param date      the appointment date, as text
     * @param time      the appointment time, as text
     */
    public Appointment(int patientId, int docId, String date, String time) {
        this.patientId = patientId;
        this.docId = docId;
        this.date = date;
        this.time = time;
    }

    /** Returns the booked patient's ID (PatientId column). */
    public int getPatientId() {
        return patientId;
    }

    /** Returns the doctor's ID (DocId column). */
    public int getDocId() {
        return docId;
    }

    /** Returns the appointment date as stored (Date column). */
    public String getDate() {
        return date;
    }

    /** Returns the appointment time as stored (Time column). */
    public String getTime() {
        return time;
    }

    /**
     * Text shown in the Appointments screen list,
     * e.g. "Patient 1082 with Doctor 171 on 2026-12-01 at 09:00".
     */
    @Override
    public String toString() {
        return "Patient " + patientId + " with Doctor " + docId + " on " + date + " at " + time;
    }
}
