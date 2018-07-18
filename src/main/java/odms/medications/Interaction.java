package odms.medications;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class Interaction {

    private String drugA;
    private String drugB;
    private Map<String, List<String>> ageInteractions;
    private Map<String, Integer>  coexistingConditions;
    private Map<String, List<String>> durationInteractions;
    private Map<String, List<String>> genderInteractions;
    private LocalDateTime dateTimeCreated;

    public Interaction(String drug1, String drug2, Map<String, List<String>> ageEffects,
            Map<String, Integer> coexistingEffects, Map<String, List<String>> durationEffects,
            Map<String, List<String>> genderEffects) {

        this.drugA = drug1;
        this.drugB = drug2;
        this.ageInteractions = ageEffects;
        this.coexistingConditions = coexistingEffects;
        this.durationInteractions = durationEffects;
        this.genderInteractions = genderEffects;
        this.dateTimeCreated = LocalDateTime.now();
    }

    public String getDrugA() { return drugA; }

    public String getDrugB() { return drugB; }

    public LocalDateTime getDateTimeExpired() { return dateTimeCreated.plusDays(7); }
}
