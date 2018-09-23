package odms.commons.model.profile;

/**
 * Model for storing HLA's.
 */
public class HLA {

    private String alphaValue;
    private Integer numericValue;
    private Boolean groupX;
    private Boolean groupY;

    public HLA(
            String alphaValue,
            Integer numericValue,
            Boolean groupX,
            Boolean groupY) {
        this.alphaValue = alphaValue;
        this.numericValue = numericValue;

        // Primary antigens cannot be both Group X and Group Y.
        // Non-Primary antigens are neither Group X or Group Y.
        if (groupX && groupY) {
            throw new UnsupportedOperationException();
        } else {
            this.groupX = groupX;
            this.groupY = groupY;
        }
    }

    public String getAlphaValue() {
        return alphaValue;
    }

    public Integer getNumericValue() {
        return numericValue;
    }

    public Boolean getGroupX() {
        return groupX;
    }

    public Boolean getGroupY() {
        return groupY;
    }
}
