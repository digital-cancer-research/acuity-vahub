package com.acuity.visualisations.common.vo;

/**
 * The dayzero date. Currently its first dose, but randomisation and others to follow
 *
 * @author ksnd199
 */
public enum DayZeroType {

    DAYS_SINCE_FIRST_DOSE,
    DAYS_SINCE_STUDY_DAY,
    DAYS_SINCE_RANDOMISATION,
    DAYS_SINCE_FIRST_TREATMENT;

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
