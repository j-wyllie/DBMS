package odms.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.profile.HLAType;
import odms.controller.database.DAOFactory;
import odms.controller.database.hla.HLADAO;

@Slf4j
public class HlaController {
    /**
     * Returns the best match of the given antigens of the given gene.
     * @param gene the gene to be matched.
     * @param hlaA the first antigen.
     * @param hlaB the second antigen.
     * @return number of matching antigens (0-2).
     */
    private static Integer calcMatch(String gene, HLAType hlaA, HLAType hlaB) {
        Integer xa = hlaA.getGroupX().get(gene);
        Integer xb = hlaA.getGroupY().get(gene);
        Integer ya = hlaB.getGroupX().get(gene);
        Integer yb = hlaB.getGroupY().get(gene);

        // Try matching same groups
        int numMatchingSame = 0;
        if (xa.equals(ya)) {
            numMatchingSame++;
        }
        if (xb.equals(yb)) {
            numMatchingSame++;
        }

        // Try matching cross groups
        int numMatchingCross = 0;
        if (xa.equals(yb)) {
            numMatchingCross++;
        }
        if (xb.equals(ya)) {
            numMatchingCross++;
        }

        return Math.max(numMatchingSame, numMatchingCross);
    }

    /**
     * Returns with a score of match fit as a percentage.
     *
     * @param hlaA first HLA to compare.
     * @param hlaB second HLA to compare.
     * @return match fit as a percentage.
     */
    private static Integer matchCalcScore(HLAType hlaA, HLAType hlaB) {
        final float matchMultiplier = 100f / 12f;
        float score;
        int numMatchingAntigens = 0;

        try {
            for (String gene : HLAType.getPrimaryGeneList()) {
                if (gene.isEmpty()) {
                    return null;
                }
                numMatchingAntigens += calcMatch(gene, hlaA, hlaB);
            }
        } catch (NullPointerException exception) {
            return null;
        }

        score = numMatchingAntigens * matchMultiplier;
        return (int) score;
    }

    /**
     * Get the secondary HLAs as a list of strings.
     *
     * @param profileID the profile ID of the HLAs.
     * @return List of secondary HLAs.
     */
    public List<String> getSecondaryHLAs(Integer profileID) {
        HLADAO hladao = DAOFactory.getHlaDAO();
        Map<String, Integer> secondaryAntigensMap = hladao.get(profileID).getSecondaryAntigens();
        List<String> secondaryAntigensList = new ArrayList<>();
        for (String gene : secondaryAntigensMap.keySet()) {
            String allele = String.valueOf(secondaryAntigensMap.get(gene));
            secondaryAntigensList.add(0, gene + allele);
        }
        return secondaryAntigensList;
    }

    /**
     * Returns the match score as a string, or message if n/a.
     *
     * @param profileIdA the profile ID of the first HLA to compare.
     * @param profileIdB the profile ID of the second HLA to compare.
     * @return the string representing the HLAs match fit.
     */
    public String getMatchString(Integer profileIdA, Integer profileIdB) {
        Integer score = matchScore(profileIdA, profileIdB);
        String text;
        if (score == null) {
            text = "Missing HLA";
        } else {
            text = String.valueOf(score) + "%";
        }
        return text;
    }

    /**
     * Returns with a score of match fit as a percentage.
     *
     * @param profileIdA the profile ID of the first HLA to compare.
     * @param profileIdB the profile ID of the second HLA to compare.
     * @return match fit as a percentage.
     */
    private static Integer matchScore(Integer profileIdA, Integer profileIdB) {

        ArrayList<HLAType> hlas = getDatabaseHLA(profileIdA, profileIdB);

        return matchCalcScore(hlas.get(0), hlas.get(1));
    }


    /**
     * Gets HLAType from database.
     *
     * @param profileIdA the profile ID of the first HLA.
     * @param profileIdB the profile ID of the second HLA.
     * @return arraylist of two HLATypes to be compared.
     */
    private static ArrayList<HLAType> getDatabaseHLA(Integer profileIdA, Integer profileIdB) {
        ArrayList<HLAType> hlas = new ArrayList<>();
        HLADAO hladao = DAOFactory.getHlaDAO();
        hlas.add(hladao.get(profileIdA));
        hlas.add(hladao.get(profileIdB));
        return hlas;
    }
}
