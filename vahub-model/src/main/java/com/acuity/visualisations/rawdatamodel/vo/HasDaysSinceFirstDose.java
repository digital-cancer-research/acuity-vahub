package com.acuity.visualisations.rawdatamodel.vo;

import com.acuity.visualisations.rawdatamodel.util.DaysUtil;

import java.util.Date;
import java.util.OptionalInt;

public interface HasDaysSinceFirstDose {

    Date getFirstTreatmentDate();

    Date getMeasurementTimePoint();

    Integer getDaysSinceFirstDoseRaw();
    
    Boolean isCalcDaysSinceFirstDoseIfNull();

    default Integer getDaysSinceFirstDose() {
        if ((isCalcDaysSinceFirstDoseIfNull() != null && isCalcDaysSinceFirstDoseIfNull()) && getDaysSinceFirstDoseRaw() == null) {
            OptionalInt res = DaysUtil.daysBetween(getFirstTreatmentDate(), getMeasurementTimePoint());
            return res.isPresent() ? res.getAsInt() : null;
        } else {
            return getDaysSinceFirstDoseRaw();
        }
    }
}
