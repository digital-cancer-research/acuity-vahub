package com.acuity.visualisations.rawdatamodel.axes;

import lombok.Getter;

public enum CountType {
    COUNT_OF_SUBJECTS(CountBase.SUBJECT),
    COUNT_OF_EVENTS(CountBase.EVENT),
    PERCENTAGE_OF_ALL_SUBJECTS(CountBase.SUBJECT),
    PERCENTAGE_OF_ALL_EVENTS(CountBase.EVENT),
    PERCENTAGE_OF_SUBJECTS_WITHIN_PLOT(CountBase.SUBJECT),
    PERCENTAGE_OF_EVENTS_WITHIN_PLOT(CountBase.EVENT),
    PERCENTAGE_OF_EVENTS_100_STACKED(CountBase.EVENT),
    PERCENTAGE_OF_SUBJECTS_100_STACKED(CountBase.SUBJECT),
    PERCENTAGE_OF_SUBJECTS_100_PERCENT_STACKED(CountBase.SUBJECT),
    CUMULATIVE_COUNT_OF_SUBJECTS(CountBase.SUBJECT),
    CUMULATIVE_COUNT_OF_EVENTS(CountBase.EVENT);

    @Getter
    private final CountBase countBase;

    CountType(CountBase countBase) {
        this.countBase = countBase;
    }

    public boolean isCumulativeType() {
        return this == CUMULATIVE_COUNT_OF_EVENTS || this == CUMULATIVE_COUNT_OF_SUBJECTS;
    }

    public enum CountBase {
        SUBJECT,
        EVENT
    }
}
