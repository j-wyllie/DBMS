package odms.commons.model.profile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import odms.commons.model.enums.OrganEnum;

/**
 * A specific procedure for use in medical history
 */
public class Procedure {

    private final String AFFECTED_ORGAN_TEXT = "Affects Donations";

    private Integer procedureId;
    private String summary;
    private LocalDate date;
    private LocalDateTime dateTime;
    private String longDescription;
    private List<OrganEnum> organsAffected = new ArrayList<>();
    private String affectsOrgansText = "Affects Donations";

    public Procedure(String summary, String date, String longDescription) {
        this.summary = summary;
        String[] dates = date.split("-");
        this.date = LocalDate.of(Integer.valueOf(dates[2]), Integer.valueOf(dates[1]),
                Integer.valueOf(dates[0]));
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

    public Procedure(int id, String summary, LocalDate date, String longDescription,
            List<OrganEnum> organs) {
        this.procedureId = id;
        this.summary = summary;
        this.date = date;
        this.longDescription = longDescription;
        this.organsAffected = new ArrayList<>(organs);
    }

    /**
     * Constructor to be used when matching an organ donor and a receiver.
     *
     * @param summary A summary of the procedure
     * @param dateTime The time of the procedure
     * @param longDescription A description of the procedure
     * @param organ The organ that is being transplanted
     */
    public Procedure(String summary, LocalDateTime dateTime, String longDescription, OrganEnum organ) {
        this.summary = summary;
        this.dateTime = dateTime;
        this.longDescription = longDescription;
        this.organsAffected.add(organ);
    }

    public Procedure(String summary, LocalDate date) {
        this(summary, date, "");
    }

    public Procedure(Integer id) {
        this.procedureId = id;
    }

    public void update() {
        if (organsAffected.size() == 0) {
            this.affectsOrgansText = "";
        } else {
            this.affectsOrgansText = AFFECTED_ORGAN_TEXT;
        }
    }

    public int getId() {
        return this.procedureId;
    }

    public String getSummary() {
        return summary;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getLongDescription() {
        return this.longDescription;
    }

    public List<OrganEnum> getOrgansAffected() {
        return organsAffected;
    }

    public String getAffectsOrgansText() {
        return affectsOrgansText;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public void setOrgansAffected(List<OrganEnum> organs) {
        this.organsAffected = organs;
    }

    /**
     * Adds an organ to the list of affected organs for this procedure
     *
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

    public void setId(Integer id) {
        this.procedureId = id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }
}
