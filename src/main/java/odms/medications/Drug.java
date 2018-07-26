package odms.medications;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates a drug with a name and a list of active ingredients.
 */
public class Drug {

    private String drugName;
    private List<String> activeIngredients = new ArrayList<>();

    public Drug(String drugName){
        this.drugName = drugName;
    }

    /**
     * Sets the active ingredients of a drug to an array list of ingredients.
     * @param activeIngredients active ingredients to be set
     */
    public void setActiveIngredients(List<String> activeIngredients){
        this.activeIngredients = activeIngredients;
    }

    public String getDrugName(){
        return this.drugName;
    }

    public List<String> getActiveIngredients(){
        return this.activeIngredients;
    }
}
