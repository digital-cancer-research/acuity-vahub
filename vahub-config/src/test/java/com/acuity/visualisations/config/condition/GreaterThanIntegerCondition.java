package com.acuity.visualisations.config.condition;

import org.assertj.core.api.Condition;

/**
 *
 * @author ksnd199
 */
public class GreaterThanIntegerCondition extends Condition<Integer> {

    private Integer greaterThan;

    public GreaterThanIntegerCondition(Integer greaterThan) {
        this.greaterThan = greaterThan;
    }

    @Override
    public boolean matches(Integer value) {
        return value > greaterThan;
    }
}
