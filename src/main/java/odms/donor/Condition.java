package odms.donor;

import java.time.LocalDate;

/**
 * A specific condition for use in medical history
 */
public class Condition {
    private String condition;
    private LocalDate dateOfDiagnosis;
    private LocalDate dateCured;
    private boolean isCured = false;
    private boolean isChronic = false;
    private String isChronicString = "";


    public Condition(boolean isCured, boolean isChronic, String condition, LocalDate dateOfDiagnosis, LocalDate dateCured) {
        this.isCured = isCured;
        this.isChronic = isChronic;
        this.condition = condition;
        this.dateOfDiagnosis = dateOfDiagnosis;
        this.dateCured = dateCured;
        if (isChronic) {isChronicString = "CHRONIC";}
    }

    Condition(String condition, LocalDate dateOfDiagnosis, LocalDate dateCured, boolean isCured, boolean isCronic) {
        this.condition = condition;
        this.dateOfDiagnosis = dateOfDiagnosis;
        this.dateCured = dateOfDiagnosis;
        this.isCured = isCured;
        this.isCronic = isCronic;
    }

    // getters
    public String getCondition() { return this.condition; }
    public LocalDate getDateOfDiagnosis() { return dateOfDiagnosis; }
    public LocalDate getDateCured() { return dateCured; }
    public boolean isCured() { return this.isCured; }
    public boolean isCronic() { return isCronic; }

    // setters
    public void setIsCured(boolean isCured) { this.isCured = isCured; }
    public void setIsCronic(boolean isCronic) { this.isCronic = isCronic; }
    public boolean isChronic() { return isChronic; }


}
