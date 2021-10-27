package com.acuity.visualisations.rawdatamodel.service.timeline.data.patient;

import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@Data
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class PatientDataEventDetails implements Serializable {
    private String measurementName;
    private Double value;
    private String unit;
    private DateDayHour startDate;
}
