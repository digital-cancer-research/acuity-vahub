package com.acuity.visualisations.rawdatamodel.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
@AcuityEntity(version = 0)
public class CardiacDecgRaw implements Serializable {
    private String id;

    private String subjectId;

    private String measurementName;

    private Date measurementTimePoint;

    private Double visitNumber;

    private String resultValue;

    private String protocolScheduleTimepoint;

    private String ecgEvaluation;

    private String clinicallySignificant;

    private String method;

    private Integer beatGroupNumber;

    private Integer beatNumberWithinBeatGroup;

    private Integer numberOfBeatsInAverageBeat;

    private Double beatGroupLengthInSec;

    private String comment;
}

