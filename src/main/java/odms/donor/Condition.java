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
    private String chronicText = "";


    public Condition(String condition, LocalDate dateOfDiagnosis, LocalDate dateCured, boolean isCured, boolean isChronic) {
        this.condition = condition;
        this.dateOfDiagnosis = dateOfDiagnosis;
        this.dateCured = dateCured;
        this.isCured = isCured;
        this.isChronic = isChronic;
        if (isChronic) {this.chronicText = "CHRONIC";}
    }

    // getters
    public String getCondition() { return this.condition; }
    public LocalDate getDateOfDiagnosis() { return dateOfDiagnosis; }
    public LocalDate getDateCured() { return dateCured; }
    public boolean getCured() { return this.isCured; }
    public boolean getChronic() { return isChronic; }
    public String getChronicText() { return chronicText; }



    // setters
    public void setIsCured(boolean isCured) { this.isCured = isCured; }
    public void setIsChronic(boolean isChronic) { this.isChronic = isChronic; }
    public void setChronicText(String chronicText) { this.chronicText = chronicText; }


}
