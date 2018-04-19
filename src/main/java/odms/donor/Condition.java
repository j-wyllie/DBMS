package odms.donor;

import java.time.LocalDate;

/**
 * A specific condition for use in medical history
 */
public class Condition {
    private String condition;
    private LocalDate dateOfDiagnosis;
    private boolean isCurrent = true;
    private boolean isCured = false;
    private boolean isCronic = false;

    public boolean isCured() { return this.isCured; }
}
