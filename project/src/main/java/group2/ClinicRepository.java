// AI-assisted (Claude): bridge between the GUI controllers and DataLink; converts DataLink's text results into model objects. Reviewed by Vince.
package group2;

import java.util.ArrayList;
import java.util.List;

/**
 * Bridge between the GUI controllers and Christopher's {@link DataLink}.
 *
 * HOW IT FITS:
 *   FXML screen -> Controller -> ClinicRepository -> DataLink -> SQLite "Data" file
 *
 * - Controllers never call DataLink directly. They call methods here and receive
 *   Doctor / Patient / Schedule / Appointment objects instead of raw strings.
 * - Apart from DataLink, this is the only class that knows the table and column
 *   names, so if the database changes, this file and DataLink are the ones to update.
 * - One shared instance is created in {@link App#start} and handed to every
 *   controller through {@link App#getRepository()}.
 *
 * READING DATA:
 * DataLink.GetData returns all values of a query joined by spaces, e.g. "171 Nova Scotia ".
 * Splitting that on spaces would break names that contain spaces. So reads happen in two steps:
 *   1. Read only the integer ID column (IDs never contain spaces, so splitting is safe).
 *   2. For each ID, read each text column separately (one value per call, so no splitting is needed).
 * At clinic scale (max 50 doctors, 500 patients) this is fast enough.
 *
 * ERRORS:
 * When input is invalid or a clinic rule would be broken, methods throw
 * IllegalArgumentException with a human-readable message. Controllers catch it and
 * show the message in the screen's status label.
 */
public class ClinicRepository {

    /** Column names of the schedules table, Monday to Sunday, in table order. */
    private static final String[] DAY_COLUMNS = {
        "MondayTime", "TuesdayTime", "WednesdayTime", "ThursdayTime",
        "FridayTime", "SaturdayTime", "SundayTime"
    };

    /** Christopher's SQL layer; every database call goes through this object. */
    private final DataLink dataLink;

    /**
     * Connects to the database and makes sure all four tables exist.
     * CreateTables uses "CREATE TABLE IF NOT EXISTS", so running it on every start is safe
     * and never wipes data. This lets the app run on a brand-new empty Data file.
     */
    public ClinicRepository() {
        dataLink = new DataLink();
        dataLink.CreateTables();
    }

    // =====================================================================
    // Doctors
    // =====================================================================

    /**
     * Reads every doctor from the doctors table.
     *
     * @return all doctors, in database order; an empty list if there are none
     */
    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        // "1 = 1" is always true, so this WHERE clause matches every row.
        for (int id : getIds("doctors", "DocID", "1 = 1")) {
            // Second step: fetch this doctor's name on its own (see class comment).
            doctors.add(new Doctor(id, getText("doctors", "DocName", "DocID = " + id)));
        }
        return doctors;
    }

    /**
     * Adds a new doctor.
     *
     * @param docId   the new doctor's ID; must not already be used
     * @param docName the doctor's name; must not be blank
     * @throws IllegalArgumentException if the name is blank, the ID is taken,
     *                                  or the database did not save the row
     */
    public void addDoctor(int docId, String docName) {
        requireText(docName, "Doctor name");
        // Check first: DataLink would only print the duplicate-key error, not report it.
        if (dataLink.SelectFind("doctors", "DocID = " + docId)) {
            throw new IllegalArgumentException("Doctor ID " + docId + " already exists.");
        }
        // InsertData takes values in column order: DocID, DocName.
        dataLink.InsertData("doctors", new String[] {String.valueOf(docId), escape(docName.trim())});
        confirmSaved("doctors", "DocID = " + docId);
    }

    // =====================================================================
    // Schedules
    // =====================================================================

    /**
     * Reads one doctor's weekly schedule.
     *
     * @param docId the doctor's ID
     * @return the schedule, or null if this doctor has no schedule row yet
     */
    public Schedule getSchedule(int docId) {
        String where = "DocID = " + docId;
        if (!dataLink.SelectFind("schedules", where)) {
            return null;
        }
        // Read each day's column one at a time (values like "5-9" or "Away").
        String[] days = new String[DAY_COLUMNS.length];
        for (int i = 0; i < DAY_COLUMNS.length; i++) {
            days[i] = getText("schedules", DAY_COLUMNS[i], where);
        }
        return new Schedule(docId, days[0], days[1], days[2], days[3], days[4], days[5], days[6]);
    }

    // =====================================================================
    // Patients
    // =====================================================================

    /**
     * Reads every patient from the patients table.
     *
     * @return all patients, in database order; an empty list if there are none
     */
    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        for (int id : getIds("patients", "PatientID", "1 = 1")) {
            patients.add(new Patient(id, getText("patients", "PatientName", "PatientID = " + id)));
        }
        return patients;
    }

    /**
     * Registers a new patient.
     *
     * @param patientId   the new patient's ID; must not already be used
     * @param patientName the patient's name; must not be blank
     * @throws IllegalArgumentException if the name is blank, the ID is taken,
     *                                  or the database did not save the row
     */
    public void addPatient(int patientId, String patientName) {
        requireText(patientName, "Patient name");
        if (dataLink.SelectFind("patients", "PatientID = " + patientId)) {
            throw new IllegalArgumentException("Patient ID " + patientId + " already exists.");
        }
        // InsertData takes values in column order: PatientID, PatientName.
        dataLink.InsertData("patients", new String[] {String.valueOf(patientId), escape(patientName.trim())});
        confirmSaved("patients", "PatientID = " + patientId);
    }

    // =====================================================================
    // Appointments
    // =====================================================================

    /** Returns every appointment in the clinic. */
    public List<Appointment> getAllAppointments() {
        return getAppointments("1 = 1");
    }

    /** Returns the appointments of one patient (at most one, see Appointment class note). */
    public List<Appointment> getAppointmentsForPatient(int patientId) {
        return getAppointments("PatientID = " + patientId);
    }

    /** Returns all appointments booked with one doctor. */
    public List<Appointment> getAppointmentsForDoctor(int docId) {
        return getAppointments("DocID = " + docId);
    }

    /**
     * Books a patient with a doctor at a date and time.
     *
     * The checks below run before inserting, because DataLink only prints SQL errors,
     * so without them a failed booking would look like it succeeded.
     *
     * @param patientId the patient to book
     * @param docId     the doctor to see
     * @param date      appointment date (free text, required)
     * @param time      appointment time (free text, required)
     * @throws IllegalArgumentException with the reason if the booking is not allowed
     */
    public void bookAppointment(int patientId, int docId, String date, String time) {
        requireText(date, "Date");
        requireText(time, "Time");
        // Clean the text once so the same safe values are used in the checks and the insert.
        String cleanDate = escape(date.trim());
        String cleanTime = escape(time.trim());

        // Rule: both the patient and the doctor must exist.
        if (!dataLink.SelectFind("patients", "PatientID = " + patientId)) {
            throw new IllegalArgumentException("Patient ID " + patientId + " does not exist.");
        }
        if (!dataLink.SelectFind("doctors", "DocID = " + docId)) {
            throw new IllegalArgumentException("Doctor ID " + docId + " does not exist.");
        }
        // Rule from the current table design: appointments.PatientId is the PRIMARY KEY,
        // so a patient can hold only one appointment at a time.
        if (dataLink.SelectFind("appointments", "PatientID = " + patientId)) {
            throw new IllegalArgumentException("Patient " + patientId + " already has an appointment.");
        }
        // Rule from the clinic spec: a time slot is unique per doctor per day.
        if (dataLink.SelectFind("appointments",
                "DocID = " + docId + " AND Date = '" + cleanDate + "' AND Time = '" + cleanTime + "'")) {
            throw new IllegalArgumentException("Doctor " + docId + " is already booked on " + date.trim()
                    + " at " + time.trim() + ".");
        }

        // InsertData takes values in column order: PatientID, DocID, Date, Time.
        dataLink.InsertData("appointments",
                new String[] {String.valueOf(patientId), String.valueOf(docId), cleanDate, cleanTime});
        confirmSaved("appointments", "PatientID = " + patientId);
    }

    /**
     * Cancels (deletes) a patient's appointment.
     *
     * @param patientId the patient whose appointment is cancelled
     * @throws IllegalArgumentException if the patient has no appointment
     */
    public void cancelAppointment(int patientId) {
        if (!dataLink.SelectFind("appointments", "PatientID = " + patientId)) {
            throw new IllegalArgumentException("Patient " + patientId + " has no appointment to cancel.");
        }
        dataLink.DeleteData("appointments", "PatientID = " + patientId);
    }

    /**
     * Shared reader for the three appointment queries above.
     * Same two-step read as the class comment explains: PatientID is the row's key,
     * so it is used to fetch each remaining column of that row.
     *
     * @param where SQL condition choosing which appointments to return
     */
    private List<Appointment> getAppointments(String where) {
        List<Appointment> appointments = new ArrayList<>();
        for (int patientId : getIds("appointments", "PatientID", where)) {
            String row = "PatientID = " + patientId;
            int docId = Integer.parseInt(getText("appointments", "DocID", row));
            appointments.add(new Appointment(patientId, docId,
                    getText("appointments", "Date", row),
                    getText("appointments", "Time", row)));
        }
        return appointments;
    }

    // =====================================================================
    // Helpers around DataLink
    // =====================================================================

    /**
     * Step 1 of a read: gets one integer column for every matching row.
     * GetData returns e.g. "171 172 "; integers never contain spaces,
     * so splitting on whitespace gives exactly one ID per row.
     *
     * @param table    table to read
     * @param idColumn integer key column, e.g. "DocID"
     * @param where    SQL condition choosing the rows
     * @return the IDs; an empty list if no rows match
     */
    private List<Integer> getIds(String table, String idColumn, String where) {
        List<Integer> ids = new ArrayList<>();
        String raw = dataLink.GetData(table, new String[] {idColumn}, where).trim();
        if (!raw.isEmpty()) {
            for (String part : raw.split("\\s+")) {
                ids.add(Integer.parseInt(part));
            }
        }
        return ids;
    }

    /**
     * Step 2 of a read: gets one text column from one row.
     * The where clause must match a single row (we always pass "IdColumn = n").
     * GetData adds a trailing space, which trim() removes. An empty database cell
     * comes back as the word "null", which is turned into an empty string.
     */
    private String getText(String table, String column, String where) {
        String value = dataLink.GetData(table, new String[] {column}, where).trim();
        return value.equals("null") ? "" : value;
    }

    /**
     * Confirms that an insert really happened.
     * DataLink catches SQL errors and only prints them to the console, so after every
     * insert we look the row up again; if it is missing, the save failed.
     */
    private void confirmSaved(String table, String where) {
        if (!dataLink.SelectFind(table, where)) {
            throw new IllegalArgumentException("The database did not save the record (see console for the SQL error).");
        }
    }

    /**
     * Makes typed text safe to put inside DataLink's SQL.
     * DataLink builds commands like  VALUES('text')  by joining strings. A name such as
     * O'Brien would end the quote early and break the command (or let someone inject SQL).
     * In SQL, two single quotes '' inside a quoted string mean one literal quote,
     * so the text is stored exactly as typed.
     */
    private static String escape(String text) {
        return text.replace("'", "''");
    }

    /**
     * Throws if a required text field is missing or only spaces.
     *
     * @param field field name used in the error message, e.g. "Doctor name"
     */
    private static void requireText(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " is required.");
        }
    }
}
