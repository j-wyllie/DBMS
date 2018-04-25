package odms.profile;

import java.time.LocalDate;

/**
 * A specific procedure for use in medical history
 */
public class Procedure {
    private String name;
    private LocalDate date;
    private String summary;
    private String longDescription;

    Procedure(String name, LocalDate date, String summary, String longDescription) {
        this.name = name;
        this.date = date;
        this.summary = summary;
        this.longDescription = longDescription;
    }

    Procedure(String name, LocalDate date, String summary) {
        this(name, date, summary, "");
    }

    // getters
    public String getName() { return this.name; }
    public LocalDate getDate() { return date; }
    public String getSummary() { return summary; }
    public String getLongDescription() { return this.longDescription; }

    // setters
    public void setName(String name) { this.name = name; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setSummary(String summary) { this.summary = summary; }
    public void setLongDescription(String longDescription) { this.longDescription = longDescription; }
}
