package com.acuity.visualisations.config.condition;

import org.assertj.core.api.Condition;

/**
 *
 * @author ksnd199
 */
public class DoubleRangeCondition extends Condition<Double> {

    protected Double from;
    protected Double to;

    public DoubleRangeCondition(Double from, Double to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean matches(Double value) {
        return (from < value && value < to);
    }
}
