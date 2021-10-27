package com.acuity.visualisations.rawdatamodel.vo.timeline.ecg;

import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode
@ToString(callSuper = true)
@Builder
public class EcgTest implements Serializable {
    private String testName;
    private List<EcgDetailEvent> events;
    private DateDayHour baseline;
}
