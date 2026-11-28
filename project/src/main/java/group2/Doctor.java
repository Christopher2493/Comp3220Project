// AI-assisted (Claude): Doctor model, mirrors the doctors table (DocId, DocName). Reviewed by Vince.
package group2;

/**
 * A doctor working at the clinic.
 *
 * Maps one row of the {@code doctors} table: DocId -> docId, DocName -> docName.
 * Objects are created by {@link ClinicRepository} when it reads the database,
 * and are shown in the GUI lists (the list uses {@link #toString()}).
 *
 * The fields are final: a Doctor object is a read-only snapshot of a database row.
 * To change a doctor, the database is updated and the object is read again.
 */
public class Doctor {

    private final int docId;      // DocId column, unique per doctor
    private final String docName; // DocName column

    /**
     * Creates a doctor from the values of one {@code doctors} row.
     *
     * @param docId   the doctor's unique ID
     * @param docName the doctor's name
     */
    public Doctor(int docId, String docName) {
        this.docId = docId;
        this.docName = docName;
    }

    /** Returns the doctor's unique ID (DocId column). */
    public int getDocId() {
        return docId;
    }

    /** Returns the doctor's name (DocName column). */
    public String getDocName() {
        return docName;
    }

    /**
     * Text shown for this doctor in JavaFX lists and drop-downs,
     * e.g. "171 - Nova Scotia".
     */
    @Override
    public String toString() {
        return docId + " - " + docName;
    }
}
