package com.acuity.visualisations.config.condition;

import java.util.List;
import org.assertj.core.api.Condition;

/**
 * Asserts that the value passed in are contains in the list are in all of the List assert
 *
 * <p>
 * assertThat(newArrayList("1", "2", "3")).are(in(newArrayList("1", "2", "3", "4")));
 * assertThat(patients).extracting("sex").are(in(newArrayList("Male", "Female")));
 * </p>
 *
 * @author ksnd199
 */
public class ListContainsCondition<T> extends Condition {

    private List<T> list = null;

    public ListContainsCondition(List<T> myList) {
        list = myList;
    }

    @Override
    public boolean matches(Object value) {
        return list.contains(value);
    }
}
