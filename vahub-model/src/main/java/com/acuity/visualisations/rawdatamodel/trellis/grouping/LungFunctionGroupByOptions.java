package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.AcceptsAttributeContext;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.BinableOption;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.TimestampOption;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LungFunction;

import java.util.Objects;

public enum LungFunctionGroupByOptions implements GroupByOption<LungFunction> {

    VISIT_NUMBER(LungFunction.Attributes.VISIT_NUMBER),
    ARM(LungFunction.Attributes.ARM),

    @AcceptsAttributeContext(required = false)
    MEASUREMENT(LungFunction.Attributes.MEASUREMENT) {
        @Override
        public EntityAttribute<LungFunction> getAttribute(Params params) {
            final Object context = params == null ? null : params.get(Param.CONTEXT);
            final String unit = context == null ? null : Objects.toString(context);
            return unit == null
                    ? LungFunction.Attributes.MEASUREMENT.getAttribute()
                    : EntityAttribute.attribute("MEASUREMENT",
                    (LungFunction l) -> l.getEvent().getMeasurementName() + (l.getEvent().getUnit() == null ? "" : " (" + unit + ")"));
        }
    },
    ACTUAL_VALUE(LungFunction.Attributes.RESULT_VALUE),
    ABSOLUTE_CHANGE_FROM_BASELINE(LungFunction.Attributes.CHANGE_FROM_BASELINE),
    PERCENTAGE_CHANGE_FROM_BASELINE(LungFunction.Attributes.PERCENT_CHANGE_FROM_BASELINE),
    VISIT_DESCRIPTION(LungFunction.Attributes.VISIT_DESCRIPTION) {
        @Override
        public EntityAttribute<LungFunction> getAttribute() {
            return EntityAttribute.attribute("VISIT_DESCRIPTION", l -> new VisitDescription(l.getEvent().getVisitDescription()));
        }
    },
    @TimestampOption
    @BinableOption
    MEASUREMENT_TIME_POINT(LungFunction.Attributes.MEASUREMENT_TIME_POINT) {
        @Override
        public EntityAttribute<LungFunction> getAttribute() {
            return EntityAttribute.attribute("MEASUREMENT_TIME_POINT",
                    LungFunction::getAccessibleMeasurementTimePoint);
        }

        @Override
        public EntityAttribute<LungFunction> getAttribute(Params params) {
            return params == null ? getAttribute() : Attributes.getBinnedAttribute("MEASUREMENT_TIME_POINT", params,
                    LungFunction::getAccessibleMeasurementTimePoint);
        }
    },
    @BinableOption
    VISIT_DATE(LungFunction.Attributes.VISIT_DATE) {
        @Override
        public EntityAttribute<LungFunction> getAttribute() {
            return EntityAttribute.attribute("VISIT_DATE", l -> l.getEvent().getVisitDate());
        }

        @Override
        public EntityAttribute<LungFunction> getAttribute(Params params) {
            return params == null ? getAttribute() : Attributes.getBinnedAttribute("VISIT_DATE", params,
                    l -> l.getEvent().getVisitDate());
        }
    };

    private LungFunction.Attributes originAttribute;

    LungFunctionGroupByOptions(LungFunction.Attributes attribute) {
        this.originAttribute = attribute;
    }

    @Override
    public EntityAttribute<LungFunction> getAttribute() {
        return originAttribute.getAttribute();
    }

}

