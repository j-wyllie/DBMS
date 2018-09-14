package odms.controller;

import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

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

    public static void main (String[] args) {
        for (String hla : hlaTestStrings) {
            if (hla.matches(REGEX_CLASS_I)) {
                log.info(hla + " passed");
            } else if (hla.matches(REGEX_CLASS_II)) {
                log.info(hla + " passed");
            } else {
                log.info(hla + " failed");
            }
        }
    }
}
