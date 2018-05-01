package odms.profile;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * A specific procedure for use in medical history
 */
public class Procedure {
    private String summary;
    private LocalDate date;
    private String longDescription;
    private ArrayList<Organ> organsAffected = new ArrayList<>();

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
    public ArrayList<Organ> getOrgansAffected() {
        return organsAffected;
    }

    // setters
    public void setSummary(String summary) { this.summary = summary; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setLongDescription(String longDescription) { this.longDescription = longDescription; }

    // organ methods

    /**
     * Adds an organ to the list of affected organs for this procedure
     * @param profile
     * @param organ
     * @throws IllegalArgumentException if the organ is not a donated organ
     */
    public void addAffectedOrgan(Profile profile, Organ organ) throws IllegalArgumentException {
        if (profile.getDonatedOrgans().contains(organ)) {
            organsAffected.add(organ);
        } else {
            throw new IllegalArgumentException("No an organ donated by this profile");
        }
    }
    public void removeAffectedOragen(Organ organ) {
        organsAffected.remove(organ);
    }
}
