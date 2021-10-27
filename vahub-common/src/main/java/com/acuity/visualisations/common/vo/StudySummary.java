package com.acuity.visualisations.common.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by knml167 on 10/07/2014.
 */
@Data
@EqualsAndHashCode
@ToString
public class StudySummary  implements Serializable {
    private String project;
    private String study;
    private String part;
    private Integer patientsCount;

}
