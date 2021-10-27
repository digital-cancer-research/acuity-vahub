package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.util.Constants;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.exposure.CycleDose;
import com.acuity.visualisations.rawdatamodel.vo.exposure.ExposureData;
import com.acuity.visualisations.rawdatamodel.vo.exposure.SubjectCycle;
import com.acuity.visualisations.rawdatamodel.vo.exposure.VisitDose;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exposure;

import static com.acuity.visualisations.rawdatamodel.util.Attributes.defaultNullableValue;

public enum ExposureGroupByOptions implements GroupByOption<Exposure> {

    NONE(null) {
        @Override
        public EntityAttribute<Exposure> getAttribute() {
            return EntityAttribute.attribute("NONE", e -> Constants.NONE);
        }
    },
    ANALYTE_CONCENTRATION(Exposure.Attributes.ANALYTE_CONCENTRATION),
    TIME_FROM_ADMINISTRATION(Exposure.Attributes.TIME_FROM_ADMINISTRATION),
    SUBJECT(Exposure.Attributes.SUBJECT),
    ANALYTE(Exposure.Attributes.ANALYTE) {
        @Override
        public ExposureGroupByOptions[] getColorByOptions() {
            return new ExposureGroupByOptions[]{NONE};
        }
    },
    CYCLE(null) {
        @Override
        public EntityAttribute<Exposure> getAttribute() {
            return EntityAttribute.attribute("CYCLE", e -> e.getEvent().getCycle().getTreatmentCycle());
        }
    },
    SUBJECT_CYCLE(Exposure.Attributes.SUBJECT_CYCLE) {
        @Override
        public ExposureGroupByOptions[] getColorByOptions() {
            return new ExposureGroupByOptions[]{SUBJECT, CYCLE, DOSE, DAY, VISIT};
        }
    },
    ALL_INFO(null) {
        @Override
        public EntityAttribute<Exposure> getAttribute() {
            return EntityAttribute.attribute("ALL_INFO",
                    e -> new ExposureData((SubjectCycle) Attributes.get(SUBJECT_CYCLE.getAttribute(), e), e));
        }
    },
    DOSE(Exposure.Attributes.TREATMENT),
    VISIT(Exposure.Attributes.VISIT),
    DAY(Exposure.Attributes.DAY),
    DOSE_PER_VISIT(null) {
        @Override
        public EntityAttribute<Exposure> getAttribute() {
            return EntityAttribute.attribute("DOSE_PER_VISIT",
                    e -> new VisitDose(defaultNullableValue(e.getEvent().getTreatment()).toString(),
                            defaultNullableValue(e.getEvent().getCycle().getVisit())));
        }
    },
    DOSE_PER_CYCLE(null) {
        @Override
        public EntityAttribute<Exposure> getAttribute() {
            return EntityAttribute.attribute("DOSE_PER_CYCLE",
                    e -> new CycleDose(e.getEvent().getTreatment(),
                            e.getEvent().getTreatmentCycle()));
        }
    };

    private Exposure.Attributes origin;
    ExposureGroupByOptions(Exposure.Attributes origin) {
        this.origin = origin;
    }

    public ExposureGroupByOptions[] getColorByOptions() {
        return new ExposureGroupByOptions[]{this};
    }

    @Override
    public EntityAttribute<Exposure> getAttribute() {
        return origin.getAttribute();
    }

}
