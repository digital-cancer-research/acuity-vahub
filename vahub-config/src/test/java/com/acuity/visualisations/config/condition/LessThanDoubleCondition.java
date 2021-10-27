package com.acuity.visualisations.config.condition;

import org.assertj.core.api.Condition;

/**
 *
 * @author ksnd199
 */
public class LessThanDoubleCondition extends Condition<Double> {

    private Double lessThan;

    public LessThanDoubleCondition(Double lessThan) {
        this.lessThan = lessThan;
    }

    @Override
    public boolean matches(Double value) {
        return value < lessThan;
    }
}
