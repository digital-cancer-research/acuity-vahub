package com.acuity.visualisations.common.vo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateMinMax implements Serializable {

    private Date min;
    private Date max;

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
     * @param minMax
     * @return
     */
    public static boolean isNotNull(DateMinMax minMax) {
        return minMax != null && minMax.isNotNull();
    }

    public LocalDate getMinLocalDate() {
        return min.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public LocalDate getMaxLocalDate() {
        return max.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
