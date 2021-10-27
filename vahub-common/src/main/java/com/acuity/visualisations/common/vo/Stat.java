package com.acuity.visualisations.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Set of statistical values used for box-and-whiskers plots - please see more here:
 *
 * http://en.wikipedia.org/wiki/Box_plot
 * http://www.mathsisfun.com/data/quartiles.html
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Stat implements Serializable {

    /**
     * day, week or visit number
     */
    private Number x;

    /** the middle value of the set of data, or from 25% to 75% - please see more here
     *  http://en.wikipedia.org/wiki/Quartile
     */
    private Double median;

    /**
     * from 75% to 100% - please see more here http://en.wikipedia.org/wiki/Quartile
     */
    private Double upperQuartile;

    /**
     * from 0% to 25% - please see more here http://en.wikipedia.org/wiki/Quartile
     */
    private Double lowerQuartile;

    /**
     * the highest datum still within 1.5 IQR of the upper quartile (Tukey)
     */
    private Double upperWhisker;

    /**
     * the lowest datum still within 1.5 IQR of the lower quartile (Tukey)
     */
    private Double lowerWhisker;

    /**
     * Set of observation points that is distant from other observations.
     */
    private Set<Double> outliers;
}
