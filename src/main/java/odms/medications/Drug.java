package odms.medications;

import java.util.ArrayList;

/**
 * Creates a drug with a name and a list of active ingredients.
 */
public class Drug {

    private String drugName;
    private ArrayList<String> activeIngredients = new ArrayList<>();

    public Drug(String drugName){
        this.drugName = drugName;
    }

    /**
     * Sets the active ingredients of a drug to an array list of ingredients.
     * @param activeIngredients active ingredients to be set
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
}
