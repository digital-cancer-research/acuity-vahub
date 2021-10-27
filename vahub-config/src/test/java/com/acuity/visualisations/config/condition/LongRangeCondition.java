package com.acuity.visualisations.config.condition;

import org.assertj.core.api.Condition;

/**
 *
 * @author ksnd199
 */
public class LongRangeCondition extends Condition<Long> {

    protected Long from;
    protected Long to;

    public LongRangeCondition(Long from, Long to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean matches(Long value) {
        return (from < value && value < to);
    }
}
