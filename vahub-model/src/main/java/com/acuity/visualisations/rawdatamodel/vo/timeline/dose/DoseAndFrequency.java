package com.acuity.visualisations.rawdatamodel.vo.timeline.dose;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * Original source: com.acuity.visualisations.model.timeline.doses.vo.DoseAndFrequency
 * @author ksnd199
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"drug", "dose", "doseUnit", "frequency"})
@ToString
public class DoseAndFrequency implements Serializable {

    /**
     * Drug, ie STDY4321
     */
    private String drug;
    /**
     * Total dose, ie 20
     */
    private Double dose;
    /**
     * Units of dose, ie mg
     */
    private String doseUnit;
    /**
     * Frequency
     */
    private Frequency frequency;

    /**
     * used to calculate the DosePerDay
     *
     * @return
     */
    @JsonIgnore
    public boolean hasFrequency() {
        return frequency != null && frequency.isValid();
    }

    /**
     * frequency use frequencyRank * dose.
     *
     * If 2-per-day on 20mg would be DosePerDay = 40mg
     *
     * @return dose per day
     */
    @JsonIgnore
    public double getDosePerDay() {
            return hasFrequency() ? 0 : frequency.getRank() * dose;
    }

    /**
     * Freq is null, so just use dose
     */
    @JsonIgnore
    public double getDosePerAdmin() {
        return dose;
    }
}
