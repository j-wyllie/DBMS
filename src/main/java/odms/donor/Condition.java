package odms.donor;

/**
 * A specific condition for use in medical history
 */
public class Condition {
    private String condition;
    private boolean isCurrent = true;
    private boolean isCured = false;
    private boolean isCronic = false;

    public boolean isCured() { return this.isCured; }
}
