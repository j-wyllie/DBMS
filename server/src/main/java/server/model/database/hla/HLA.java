package server.model.database.hla;

/**
 * Model for storing HLA's.
 */
public class HLA {

    private String alphaValue;
    private Integer numericValue;
    private Boolean groupX;
    private Boolean groupY;

    /**
     * HLA Model.
     *
     * @param alphaValue the alpha value.
     * @param numericValue the numeric value.
     * @param groupX if associated with group X.
     * @param groupY if associated with group Y.
     */
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
