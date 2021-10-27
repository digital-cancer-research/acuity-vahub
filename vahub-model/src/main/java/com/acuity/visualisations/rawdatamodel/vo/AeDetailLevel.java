package com.acuity.visualisations.rawdatamodel.vo;

/**
 * Created by knml167 on 11/7/2016.
 */
public enum AeDetailLevel {
    
    PER_INCIDENCE("Per AE Incidence"),
    PER_SEVERITY_CHANGE("Per AE Severity Change");

    private final String display;

    AeDetailLevel(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return display;
    }
}
