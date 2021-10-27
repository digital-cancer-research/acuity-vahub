package com.acuity.visualisations.rawdatamodel.vo;

import com.acuity.visualisations.rawdatamodel.util.ReferenceRangeUtil;

import java.util.Optional;
import java.util.OptionalDouble;

public interface HasReferenceRange {

    Double getResultValue();
    Double getRefLow();
    Double getRefHigh();

    default String getOutOfRefRange() {
        Optional<String> res = ReferenceRangeUtil.outOfReferenceRange(getResultValue(), getRefLow(), getRefHigh());
        return res.isPresent() ? res.get() : null;
    }

    default Double getReferenceRangeNormalisedValue() {
        OptionalDouble res = ReferenceRangeUtil.referenceRangeNormalisedValue(getResultValue(), getRefLow(), getRefHigh());
        return res.isPresent() ? res.getAsDouble() : null;
    }

    default Double getTimesUpperReferenceRange() {
        OptionalDouble res = ReferenceRangeUtil.timesReferenceRange(getResultValue(), getRefHigh());
        return res.isPresent() ? res.getAsDouble() : null;
    }

    default Double getTimesLowerReferenceRange() {
        OptionalDouble res = ReferenceRangeUtil.timesReferenceRange(getResultValue(), getRefLow());
        return res.isPresent() ? res.getAsDouble() : null;
    }
}
