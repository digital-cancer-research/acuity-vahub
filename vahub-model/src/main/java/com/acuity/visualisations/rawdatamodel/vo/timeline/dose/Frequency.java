package com.acuity.visualisations.rawdatamodel.vo.timeline.dose;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author ksnd199
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"name"})
@ToString
public class Frequency implements Serializable {

    private static final Frequency NONE = new Frequency("N/A", 0);

    /**
     * CDash equivalent of frequencyAndUnit, 1-per-day = QD
     * <p>
     * See RCT-3938
     */
    private String name;
    /**
     * CDash daily normalised freq, calculated in etl Ie old frequency_daily_normalized, ie 1-per-hour = 24, 1-per-day = 1
     * <p>
     * See RCT-3938
     */
    private double rank;

    /**
     * used to calculate the DosePerDay
     *
     * @return
     */
    @JsonIgnore
    public boolean isValid() {
        return name != null;
    }

    public static Frequency none() {
        return NONE;
    }
}
