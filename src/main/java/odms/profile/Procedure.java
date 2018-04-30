package odms.profile;

import java.time.LocalDate;

/**
 * A specific procedure for use in medical history
 */
public class Procedure {
    private String summary;
    private LocalDate date;
    private String longDescription;

    public Procedure(String summary, String date, String longDescription) {
        this.summary = summary;
        String[] dates = date.split("-");
        this.date = LocalDate.of(Integer.valueOf(dates[2]), Integer.valueOf(dates[1]), Integer.valueOf(dates[0]));
        this.longDescription = longDescription;
    }

    public Procedure(String summary, String date) {
        this(summary, date, "");
    }

    // getters
    public String getSummary() { return summary; }
    public LocalDate getDate() { return date; }
    public String getLongDescription() { return this.longDescription; }

    // setters
    public void setSummary(String summary) { this.summary = summary; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setLongDescription(String longDescription) { this.longDescription = longDescription; }
}
