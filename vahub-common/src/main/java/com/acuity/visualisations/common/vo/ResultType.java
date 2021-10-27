package com.acuity.visualisations.common.vo;

/**
 * enum representing the y-axis choice for the labs plots
 */
public enum ResultType {
    RESULT_VALUE,
    REF_RANGE_NORMALISATION,
    PERCENT_CHANGE_FROM_BASELINE,
    ULN_NORMALISATION,
    DYNAMIC_NORMALISATION;

    /**
     * Check if the name of enum instance is equals to the given name
     *
     * @param name enum item name
     * @return true if equals
     */
    public boolean is(String name) {
        return name().equals(name);
    }
}
