package odms.commons.model.profile;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HLAType {
    // validation regex
    private static final String REGEX_GENE_CLASS_I = "[A-C]";
    private static final String REGEX_GENE_CLASS_II = "(DP|DQ|DR)";
    private static final String REGEX_ALLELE = "[0-9]{1,3}";
    private static final String REGEX_CLASS_I = "(" + REGEX_GENE_CLASS_I + REGEX_ALLELE + ")";
    private static final String REGEX_CLASS_II = "(" + REGEX_GENE_CLASS_II + REGEX_ALLELE + ")";
    private static final String REGEX_VALID_ALL = "((" + REGEX_GENE_CLASS_I +
            "|" + REGEX_GENE_CLASS_II +
            ")" + REGEX_ALLELE + ")";

    private static List<String> primaryGeneList = Arrays.asList("A", "B", "C", "DP", "DQ", "DR");
    private Map<String, Integer> groupX = new HashMap<>();
    private Map<String, Integer> groupY = new HashMap<>();
    private Map<String, Integer> secondaryAntigens = new HashMap<>();

    /**
     * Initialises the keys of the HLA type
     */
    public HLAType() {
        // initialise group X keys
        groupX.put("A", null);
        groupX.put("B", null);
        groupX.put("C", null);
        groupX.put("DP", null);
        groupX.put("DQ", null);
        groupX.put("DR", null);

        // initialise group Y keys
        groupY.put("A", null);
        groupY.put("B", null);
        groupY.put("C", null);
        groupY.put("DP", null);
        groupY.put("DQ", null);
        groupY.put("DR", null);
    }

    /**
     *  Initialises the key-value pairs of the HLA type
     * @param xa  Group X gene A  Allele
     * @param xb  Group X gene B  Allele
     * @param xc  Group X gene C  Allele
     * @param xdp Group X gene DP Allele
     * @param xdq Group X gene DQ Allele
     * @param xdr Group X gene DR Allele
     * @param ya  Group Y gene A  Allele
     * @param yb  Group Y gene B  Allele
     * @param yc  Group Y gene C  Allele
     * @param ydp Group Y gene DQ Allele
     * @param ydq Group Y gene DP Allele
     * @param ydr Group Y gene DR Allele
     */
    public HLAType(Integer xa, Integer xb, Integer xc, Integer xdp, Integer xdq, Integer xdr,
            Integer ya, Integer yb, Integer yc, Integer ydp, Integer ydq, Integer ydr) {
        // initialise group X
        groupX.put("A", xa);
        groupX.put("B", xb);
        groupX.put("C", xc);
        groupX.put("DP", xdp);
        groupX.put("DQ", xdq);
        groupX.put("DR", xdr);

        // initialise group Y
        groupY.put("A", ya);
        groupY.put("B", yb);
        groupY.put("C", yc);
        groupY.put("DP", ydp);
        groupY.put("DQ", ydq);
        groupY.put("DR", ydr);
    }

    public static List<String> getPrimaryGeneList() { return primaryGeneList; }

    public Map<String, Integer> getGroupX() { return groupX; }
    public Map<String, Integer> getGroupY() { return groupY; }

    public void setGroupX(Map<String, Integer> map) {groupX = map;}
    public void setGroupY(Map<String, Integer> map) {groupY = map;}
    public void setSecondaryAntigens(Map<String, Integer> map) {secondaryAntigens = map;}

    public void addSecondaryAntigen(String gene, Integer allele) {
        secondaryAntigens.put(gene, allele);
    }
    public Map getSecondaryAntigens() {
        return secondaryAntigens;
    }
}

