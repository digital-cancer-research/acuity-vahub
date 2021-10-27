package com.acuity.visualisations.config.condition;

import org.assertj.core.api.Condition;

/**
 *
 * @author ksnd199
 */
public class IntRangeCondition extends Condition<Integer> {

    protected Integer from;
    protected Integer to;

    public IntRangeCondition(Integer from, Integer to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean matches(Integer value) {
        return (from < value && value < to);
    }
}
