package com.acuity.visualisations.common.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MinMax implements Serializable {

    private Object min;
    private Object max;

    /**
     * Is this a null object
     */
    public boolean isNull() {
        return min == null && max == null;
    }

    /**
     * Is this not a null object
     */
    public boolean isNotNull() {
        return !isNull();
    }

    /**
     * Is this not a null object
     */
    public static boolean isNotNull(MinMax minMax) {
        return minMax != null && minMax.isNotNull();
    }
}
