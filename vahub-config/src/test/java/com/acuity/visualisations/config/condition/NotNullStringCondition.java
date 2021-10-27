package com.acuity.visualisations.config.condition;

import org.assertj.core.api.Condition;

/**
 *
 * @author ksnd199
 */
public class NotNullStringCondition extends Condition<String> {

    @Override
    public boolean matches(String value) {
        return null != value;
    }
}
