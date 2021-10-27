/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
