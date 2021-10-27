package com.acuity.visualisations.rawdatamodel.vo;

import org.apache.commons.math3.util.Precision;

import static com.acuity.visualisations.rawdatamodel.util.Constants.ROUNDING_PRECISION;

public interface HasValueAndBaseline {

    Double getResultValue();

    Double getBaselineValue();

    Double getChangeFromBaselineRaw();

    Boolean getCalcChangeFromBaselineIfNull();

    default Double getPercentChangeFromBaseline() {

        Double changeFromBaseline = getChangeFromBaselineWithoutPrecision();

        if (changeFromBaseline == null || getBaselineValue() == null || getBaselineValue() == 0.) {
            return null;
        } 
        
        if (changeFromBaseline == 0.) {
            return 0.;
        }

        return Precision.round(changeFromBaseline / getBaselineValue() * 100, ROUNDING_PRECISION);
    }

    default Double getChangeFromBaseline() {

        Double changeFromBaseline = getChangeFromBaselineWithoutPrecision();

        if (changeFromBaseline == null) {
            return null;
        } else {
            return Precision.round(changeFromBaseline, ROUNDING_PRECISION);
        }
    }

    default Double getChangeFromBaselineWithoutPrecision() {
        if ((getCalcChangeFromBaselineIfNull() != null && getCalcChangeFromBaselineIfNull()) && getChangeFromBaselineRaw() == null) {
            if (getBaselineValue() == null || getResultValue() == null) {
                return null;
            }
            return getResultValue() - getBaselineValue();
        } else {
            return getChangeFromBaselineRaw();
        }
    }
}
