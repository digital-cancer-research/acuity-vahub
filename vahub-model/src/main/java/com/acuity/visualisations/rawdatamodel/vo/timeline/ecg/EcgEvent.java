package com.acuity.visualisations.rawdatamodel.vo.timeline.ecg;

import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EcgEvent implements Serializable {
    private DateDayHour start;
    private Double visitNumber;
    private String abnormality;
    private String significant;
}
