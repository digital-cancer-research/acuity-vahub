package com.acuity.visualisations.config.condition;

import org.assertj.core.api.Condition;

/**
 *
 * @author ksnd199
 */
public class LessThanIntegerCondition extends Condition<Integer> {

    private Integer lessThan;

    public LessThanIntegerCondition(Integer lessThan) {
        this.lessThan = lessThan;
    }

    @Override
    public boolean matches(Integer value) {
        return value < lessThan;
    }
}
