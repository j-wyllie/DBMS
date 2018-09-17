package odms.controller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.profile.HLAType;

@Slf4j
public class HlaController {
    private static final String REGEX_GENE_CLASS_I = "[A-C]";
    private static final String REGEX_GENE_CLASS_II = "(DP|DQ|DR)";
    private static final String REGEX_ALLELE = "[0-9]{1,3}";
    private static final String REGEX_CLASS_I = "(" + REGEX_GENE_CLASS_I + REGEX_ALLELE + ")";
    private static final String REGEX_CLASS_II = "(" + REGEX_GENE_CLASS_II + REGEX_ALLELE + ")";
    private static final String REGEX_VALID_ALL = "((" + REGEX_GENE_CLASS_I +
            "|" + REGEX_GENE_CLASS_II +
            ")" + REGEX_ALLELE + ")";

    private static List<String> hlaTestStrings = Arrays.asList(
            "A4",
            "A45",
            "A690",
            "B9",
            "B90",
            "B901",
            "C1",
            "C119",
            "C16",
            "DP1",
            "DP16",
            "DP130",
            "DQ3",
            "DQ12",
            "DQ923",
            "DR0",
            "DR90",
            "DR990",
            "DR",
            "990",
            "(*&^@$(*&^#$(*#&",
            "D",
            "A"
    );

    /**
     * Returns with a score of match fit as a percentage.
     *
     * @param hla1 first HLA to compare
     * @param hla2 second HLA to compare
     * @return match fit as a percentage
     */
    public static int matchScore(HLAType hla1, HLAType hla2) {
        final float GENE_MATCH_MULTIPLIER = 100f / 12f;
        float score = 0;
        int numMatchingGenes = 0;

        numMatchingGenes += numUniqueMatches(hla1.getG1c1(), hla2.getG1c1());
        numMatchingGenes += numUniqueMatches(hla1.getG1c2(), hla2.getG1c2());
        numMatchingGenes += numUniqueMatches(hla1.getG2c1(), hla2.getG2c1());
        numMatchingGenes += numUniqueMatches(hla1.getG2c2(), hla2.getG2c2());

        score = numMatchingGenes; //* GENE_MATCH_MULTIPLIER;
        return (int)score;
    }

    /**
     * Returns the number of matching genes in a given class and group (set of 3 antigens)
     * of two different HLA's
     *
     * @param hlaA first to compare
     * @param hlaB second to compare
     * @return number of matching gene pairs
     */
    private static int numUniqueMatches(Set<String> hlaA, Set<String> hlaB) {
        int numMatchingGenes = 0;
        Set<String> alreadyMatched = new HashSet<>();
        for (String antigenA : hlaA) {
            String geneA = antigenA.replaceAll("[\\d]+", "");
            System.out.println("gene A  " + geneA);
            for (String antigenB : hlaB) {
                String geneB = antigenB.replaceAll("[\\d]+", "");
                System.out.println("gene B  " + geneB);
                if (geneA.equals(geneB) && !alreadyMatched.contains(geneA)) {
                    numMatchingGenes += 1;
                    alreadyMatched.add(geneA);
                }
            }
        }
        return numMatchingGenes;
    }

    public static void main (String[] args) {

        // regex testing
        for (String hla : hlaTestStrings) {
            if (hla.matches(REGEX_CLASS_I)) {
                log.info(hla + " passed");
            } else if (hla.matches(REGEX_CLASS_II)) {
                log.info(hla + " passed");
            } else {
                log.info(hla + " failed");
            }
        }


        // hla matching testing
        HLAType hlaA = new HLAType();
        hlaA.setG1c1(new HashSet<>(Arrays.asList("A123", "B123", "C123")));
        hlaA.setG1c2(new HashSet<>(Arrays.asList("DP123", "DQ123", "DR123")));
        hlaA.setG2c1(new HashSet<>(Arrays.asList("A123", "B223", "C123")));
        hlaA.setG2c2(new HashSet<>(Arrays.asList("DP123", "DQ123", "DR123")));

        HLAType hlaB = new HLAType();
        hlaB.setG1c1(new HashSet<>(Arrays.asList("B123", "B123", "B123")));
        hlaB.setG1c2(new HashSet<>(Arrays.asList("DP123", "DQ123", "DR123")));
        hlaB.setG2c1(new HashSet<>(Arrays.asList("A123", "B123", "C123")));
        hlaB.setG2c2(new HashSet<>(Arrays.asList("DP123", "DQ123", "DR123")));

        System.out.println(matchScore(hlaA, hlaB));
    }
}
