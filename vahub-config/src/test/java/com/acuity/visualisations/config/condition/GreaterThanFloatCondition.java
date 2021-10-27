package com.acuity.visualisations.config.condition;

import org.assertj.core.api.Condition;

/**
 *
 * @author ksnd199
 */
public class GreaterThanFloatCondition extends Condition<Float> {

    private Float greaterThan;

    public GreaterThanFloatCondition(Float greaterThan) {
        this.greaterThan = greaterThan;
    }

    @Override
    public boolean matches(Float value) {
        return value > greaterThan;
    }
}
