package odms.profile;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * A specific procedure for use in medical history
 */
public class Procedure {
    private final String AFFECTED_ORGAN_TEXT = "Affects Donations";

    private String summary;
    private LocalDate date;
    private String longDescription;
    private ArrayList<Organ> organsAffected = new ArrayList<>();
    private String affectsOrgansText = "Affects Donations";

    public Procedure(String summary, String date, String longDescription) {
        this.summary = summary;
        String[] dates = date.split("-");
        this.date = LocalDate.of(Integer.valueOf(dates[2]), Integer.valueOf(dates[1]), Integer.valueOf(dates[0]));
        this.longDescription = longDescription;
    }

    public Procedure(String summary, String date) {
        this(summary, date, "");
    }

    public void update() {
        if (organsAffected.size() == 0) {
            this.affectsOrgansText = "";
        } else {
            this.affectsOrgansText = AFFECTED_ORGAN_TEXT;
        }
    }

    // getters
    public String getSummary() { return summary; }
    public LocalDate getDate() { return date; }
    public String getLongDescription() { return this.longDescription; }
    public ArrayList<Organ> getOrgansAffected() { return organsAffected; }
    public String getAffectsOrgansText() { return affectsOrgansText; }

    // setters
    public void setSummary(String summary) { this.summary = summary; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setLongDescription(String longDescription) { this.longDescription = longDescription; }
    public void setOrgansAffected(ArrayList<Organ> organs) { this.organsAffected = organs; }

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
    public void removeAffectedOrgen(Organ organ) {
        organsAffected.remove(organ);
    }
}
