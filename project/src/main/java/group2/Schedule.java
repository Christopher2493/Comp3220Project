// AI-assisted (Claude): Schedule model, mirrors the schedules table (DocId + one time string per weekday). Reviewed by Vince.
package group2;

/**
 * A doctor's weekly working hours.
 *
 * Maps one row of the {@code schedules} table: DocId plus one text column per day
 * (MondayTime ... SundayTime). Each day is stored exactly as the database holds it,
 * as free text such as "5-9" or "Away"; nothing is parsed into real times yet.
 *
 * There is at most one schedule per doctor (DocId is the table's primary key).
 * {@link ClinicRepository#getSchedule(int)} returns null when a doctor has none.
 */
public class Schedule {

    private final int docId;        // DocId column: which doctor this schedule belongs to
    private final String monday;    // MondayTime column
    private final String tuesday;   // TuesdayTime column
    private final String wednesday; // WednesdayTime column
    private final String thursday;  // ThursdayTime column
    private final String friday;    // FridayTime column
    private final String saturday;  // SaturdayTime column (the table has it; the clinic spec is Mon-Fri)
    private final String sunday;    // SundayTime column  (same note as Saturday)

    /**
     * Creates a schedule from the values of one {@code schedules} row.
     * Parameters are in the same order as the table's columns.
     */
    public Schedule(int docId, String monday, String tuesday, String wednesday,
                    String thursday, String friday, String saturday, String sunday) {
        this.docId = docId;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
    }

    /** Returns the ID of the doctor this schedule belongs to. */
    public int getDocId() {
        return docId;
    }

    /** Returns Monday's hours as stored (MondayTime column). */
    public String getMonday() {
        return monday;
    }

    /** Returns Tuesday's hours as stored (TuesdayTime column). */
    public String getTuesday() {
        return tuesday;
    }

    /** Returns Wednesday's hours as stored (WednesdayTime column). */
    public String getWednesday() {
        return wednesday;
    }

    /** Returns Thursday's hours as stored (ThursdayTime column). */
    public String getThursday() {
        return thursday;
    }

    /** Returns Friday's hours as stored (FridayTime column). */
    public String getFriday() {
        return friday;
    }

    /** Returns Saturday's hours as stored (SaturdayTime column). */
    public String getSaturday() {
        return saturday;
    }

    /** Returns Sunday's hours as stored (SundayTime column). */
    public String getSunday() {
        return sunday;
    }

    /**
     * Multi-line text, one day per line, used by the Doctors screen's schedule box.
     */
    @Override
    public String toString() {
        return "Mon: " + monday + "\nTue: " + tuesday + "\nWed: " + wednesday
                + "\nThu: " + thursday + "\nFri: " + friday
                + "\nSat: " + saturday + "\nSun: " + sunday;
    }
}
