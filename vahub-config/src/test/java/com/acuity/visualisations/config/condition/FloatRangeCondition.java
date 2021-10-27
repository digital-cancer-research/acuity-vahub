package com.acuity.visualisations.config.condition;

import org.assertj.core.api.Condition;

/**
 *
 * @author ksnd199
 */
public class FloatRangeCondition extends Condition<Float> {

    protected Float from;
    protected Float to;

    public FloatRangeCondition(Float from, Float to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean matches(Float value) {
        return (from < value && value < to);
    }
}
