package odms.profile;

import java.time.LocalDate;
import java.util.ArrayList;
import odms.enums.OrganEnum;

/**
 * A specific procedure for use in medical history
 */
public class Procedure {
    private final String AFFECTED_ORGAN_TEXT = "Affects Donations";

    private String summary;
    private LocalDate date;
    private String longDescription;
    private ArrayList<OrganEnum> organsAffected = new ArrayList<>();
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

    public Procedure(String summary, LocalDate date, String longDescription) {
        this.summary = summary;
        this.date = date;
        this.longDescription = longDescription;
    }

    public Procedure(String summary, LocalDate date) {
        this(summary, date, "");
    }

    public void update() {
        if (organsAffected.size() == 0) {
            this.affectsOrgansText = "";
        } else {
            this.affectsOrgansText = AFFECTED_ORGAN_TEXT;
        }
    }

    public String getSummary() { return summary; }
    public LocalDate getDate() { return date; }
    public String getLongDescription() { return this.longDescription; }
    public ArrayList<OrganEnum> getOrgansAffected() { return organsAffected; }
    public String getAffectsOrgansText() { return affectsOrgansText; }

    public void setSummary(String summary) { this.summary = summary; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setLongDescription(String longDescription) { this.longDescription = longDescription; }
    public void setOrgansAffected(ArrayList<OrganEnum> organs) { this.organsAffected = organs; }

    /**
     * Adds an organ to the list of affected organs for this procedure
     * @param profile
     * @param organ
     * @throws IllegalArgumentException if the organ is not a donated organ
     */
    public void addAffectedOrgan(Profile profile, OrganEnum organ) throws IllegalArgumentException {
        if (profile.getOrgansDonating().contains(organ)) {
            organsAffected.add(organ);
        } else {
            throw new IllegalArgumentException("Not an organ with donor status on this profile");
        }
    }

    public void removeAffectedOrgan(OrganEnum organ) {
        organsAffected.remove(organ);
    }
}
