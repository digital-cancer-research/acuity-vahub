package com.acuity.visualisations.rawdatamodel.service.timeline.data.patient;

import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode
@ToString
public class PatientDataEvent implements Serializable {

    private List<PatientDataEventDetails> details;
    private Integer numberOfEvents;
    private DateDayHour start;

    public PatientDataEvent(List<PatientDataEventDetails> details, Integer numberOfEvents, DateDayHour start) {
        this.details = details;
        this.numberOfEvents = numberOfEvents;
        this.start = start;
    }
}
