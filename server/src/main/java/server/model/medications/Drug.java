package server.model.medications;

/**
 * Creates a drug with a name and a list of active ingredients.
 */
public class Drug {

    private Integer drugId;
    private String drugName;

    /**
     * Constructor for dug class.
     * @param drugName name of the drug
     */
    public Drug(String drugName) {
        this.drugName = drugName;
    }

    /**
     * Constructor for dug class.
     * @param id id value of the drug object, provided by the database
     * @param drugName name of the drug
     */
    public Drug(int id, String drugName) {
        this.drugId = id;
        this.drugName = drugName;
    }

    public String getDrugName() {
        return this.drugName;
    }

    public Integer getId() {
        return this.drugId;
    }
}
