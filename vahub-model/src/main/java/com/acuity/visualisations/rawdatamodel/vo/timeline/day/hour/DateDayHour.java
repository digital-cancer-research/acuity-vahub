package com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour;

import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.JSON_TIMESTAMP_FORMAT;

@Data
@NoArgsConstructor
public class DateDayHour implements Comparable<DateDayHour>, Serializable {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JSON_TIMESTAMP_FORMAT, timezone = DaysUtil.GMT_TIMEZONE)
    private Date date;
    protected Double dayHour;
    protected Double doseDayHour;

    private String dayHourAsString;
    private String studyDayHourAsString;

    public DateDayHour(Date date, Double dayHour) {
        this.date = date;
        this.dayHour = dayHour;
    }

    @JsonIgnore
    public boolean isValid() {
        return (date != null && dayHour != null);
    }

    @Override
    public int compareTo(DateDayHour o) {
        return date.compareTo(o.date);
    }
}
