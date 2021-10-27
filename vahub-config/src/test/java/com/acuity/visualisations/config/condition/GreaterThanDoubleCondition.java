package com.acuity.visualisations.config.condition;

import org.assertj.core.api.Condition;

/**
 *
 * @author ksnd199
 */
public class GreaterThanDoubleCondition extends Condition<Double> {

    private Double greaterThan;

    public GreaterThanDoubleCondition(Double greaterThan) {
        this.greaterThan = greaterThan;
    }

    @Override
    public boolean matches(Double value) {
        return value > greaterThan;
    }
}
