package odms.model.profile;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import jdk.nashorn.internal.runtime.arrays.ArrayIndex;

/**
 * A specific condition for use in medication history.
 */
public class Condition {
    private int id;
    private String name;
    private LocalDate dateOfDiagnosis;
    private LocalDate dateCured = null;
    private String dateOfDiagnosisString;
    private String dateCuredString;
    private boolean isCured = false;
    private boolean isChronic = false;
    private String chronicText = "";

    /**
     * Constructor for cured conditions.
     * @param name name of the condition.
     * @param dateOfDiagnosis date of diagnosis.
     * @param dateCured date cured.
     * @param isChronic boolean for if the disease is chronic.
     * @throws IllegalArgumentException an argument that was not valid.
     */
    public Condition(String name, String dateOfDiagnosis, String dateCured, boolean isChronic) {

        this.name = name;
        dateOfDiagnosisString = dateOfDiagnosis;
        dateCuredString = dateCured;
        String[] dates = dateOfDiagnosis.split("-");
        try {
            this.dateOfDiagnosis = LocalDate
                    .of(Integer.valueOf(dates[2]), Integer.valueOf(dates[1]),
                            Integer.valueOf(dates[0]));
            if (dateCured != null && !isChronic) {
                this.isCured = true;
                dates = dateCured.split("-");
                this.dateCured = LocalDate.of(Integer.valueOf(dates[2]), Integer.valueOf(dates[1]),
                        Integer.valueOf(dates[0]));
            } else {
                this.isCured = false;
                this.dateCured = null;
            }
        } catch (ArrayIndexOutOfBoundsException | DateTimeException e) {
            throw new IllegalArgumentException(e);
        }

        this.isChronic = isChronic;
        if (isChronic) {this.chronicText = "CHRONIC";}
    }

    /**
     * Constructor for uncured conditions.
     * @param name name of the condition.
     * @param dateOfDiagnosis date of diagnosis.
     * @param isChronic is the condition chronic.
     */
    public Condition(String name, String dateOfDiagnosis, boolean isChronic)
            throws IllegalArgumentException {
        this(name, dateOfDiagnosis, null, isChronic);
    }

    public Condition(String name, LocalDate dateOfDiagnosis, LocalDate dateCured, boolean isChronic) throws IllegalArgumentException {
        this(name, dateOfDiagnosis.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), dateCured.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), isChronic);
    }

    public Condition(String name, LocalDate dateOfDiagnosis, boolean isChronic) throws IllegalArgumentException {
        this(name, dateOfDiagnosis.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), null, isChronic);
    }

    public Condition(int id, String name, LocalDate dateOfDiagnosis, boolean chronic, boolean cured, LocalDate curedDate) {
        this.id = id;
        this.name = name;
        this.dateOfDiagnosis = dateOfDiagnosis;
        this.isChronic = chronic;
        this.isCured = cured;
        this.dateCured = curedDate;

        if (isChronic) {this.chronicText = "CHRONIC";}
    }

    // getters
    public int getId() { return this.id; }
    public String getName() { return this.name; }
    public LocalDate getDateOfDiagnosis() { return dateOfDiagnosis; }
    public LocalDate getDateCured() { return dateCured; }
    public String getDateOfDiagnosisString() { return dateOfDiagnosisString; }
    public String getDateCuredString() { return dateCuredString; }
    public boolean getCured() { return this.isCured; }
    public boolean getChronic() { return isChronic; }
    public String getChronicText() { return chronicText; }


    // setters
    public void setId(int id) { this.id = id; }
    public void setIsCured(boolean isCured) { this.isCured = isCured; }
    public void setIsChronic(boolean isChronic) { this.isChronic = isChronic; }
    public void setChronicText(String chronicText) { this.chronicText = chronicText; }
    public void setDateOfDiagnosis(LocalDate dateOfDiagnosis) { this.dateOfDiagnosis = dateOfDiagnosis; }
    public void setDateCured(LocalDate dateCured) { this.dateCured = dateCured; }
    public void setName(String name) { this.name = name; }


}
