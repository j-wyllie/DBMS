package odms.medications;

import java.util.ArrayList;

/**
 * Creates a drug with a name and a list of active ingredients.
 */
public class Drug {

    private int drugId;
    private String drugName;
    private ArrayList<String> activeIngredients = new ArrayList<>();

    public Drug(String drugName){
        this.drugName = drugName;
    }

    public Drug(int id, String drugName){
        this.drugId = id;
        this.drugName = drugName;
    }

    /**
     * Sets the active ingredients of a drug to an array list of ingredients.
     * @param activeIngredients active ingredients to be set.
     */
    public void setActiveIngredients(ArrayList<String> activeIngredients){
        this.activeIngredients = activeIngredients;
    }

    public String getDrugName(){
        return this.drugName;
    }

    public ArrayList<String> getActiveIngredients(){
        return this.activeIngredients;
    }

    public int getId() { return this.drugId; }
}
