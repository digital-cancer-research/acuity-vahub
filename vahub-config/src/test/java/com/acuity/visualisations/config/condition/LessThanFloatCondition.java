package com.acuity.visualisations.config.condition;

import org.assertj.core.api.Condition;

/**
 *
 * @author ksnd199
 */
public class LessThanFloatCondition extends Condition<Float> {

    private Float lessThan;

    public LessThanFloatCondition(Float lessThan) {
        this.lessThan = lessThan;
    }

    @Override
    public boolean matches(Float value) {
        return value < lessThan;
    }
}
