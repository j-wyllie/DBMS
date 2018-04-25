package odms.donor;

import java.time.LocalDate;

/**
 * A specific condition for use in medical history
 */
public class Condition {
    private String name;
    private LocalDate dateOfDiagnosis;
    private LocalDate dateCured = null;
    private boolean isCured = false;
    private boolean isChronic = false;
    private String chronicText = "";

    /**
     * Constructor for cured conditions
     * @param name
     * @param dateOfDiagnosis
     * @param dateCured
     * @param isChronic
     */
    public Condition(String name, LocalDate dateOfDiagnosis, LocalDate dateCured, boolean isChronic) {
        this.name = name;
        this.dateOfDiagnosis = dateOfDiagnosis;
        this.dateCured = dateCured;
        if (dateCured != null) {
            this.isCured = true;
        } else {
            this.isCured = false;
        }
        this.isChronic = isChronic;
        if (isChronic) {this.chronicText = "CHRONIC";}
    }

    /**
     * Constructor for uncured conditions
     * @param name
     * @param dateOfDiagnosis
     * @param isChronic
     */
    public Condition(String name, LocalDate dateOfDiagnosis, boolean isChronic) {
        this(name, dateOfDiagnosis, null, isChronic);
    }

    // getters
    public String getName() { return this.name; }
    public LocalDate getDateOfDiagnosis() { return dateOfDiagnosis; }
    public LocalDate getDateCured() { return dateCured; }
    public boolean getCured() { return this.isCured; }
    public boolean getChronic() { return isChronic; }
    public String getChronicText() { return chronicText; }



    // setters
    public void setIsCured(boolean isCured) { this.isCured = isCured; }
    public void setIsChronic(boolean isChronic) { this.isChronic = isChronic; }
    public void setChronicText(String chronicText) { this.chronicText = chronicText; }
    public void setDateOfDiagnosis(LocalDate dateOfDiagnosis) { this.dateOfDiagnosis = dateOfDiagnosis; }
    public void setDateCured(LocalDate dateCured) { this.dateCured = dateCured; }



}
