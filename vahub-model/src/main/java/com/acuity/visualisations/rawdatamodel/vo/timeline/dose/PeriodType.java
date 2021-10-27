package com.acuity.visualisations.rawdatamodel.vo.timeline.dose;

import java.io.Serializable;

public enum PeriodType implements Serializable {
    ACTIVE("ACTIVE_DOSING"), INACTIVE("INACTIVE_DOSING"), DISCONTINUED("DISCONTINUED"), ONGOING("ONGOING_DOSING");

    private String dbValue;

    PeriodType(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }
}
