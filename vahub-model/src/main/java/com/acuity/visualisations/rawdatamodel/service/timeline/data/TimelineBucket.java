package com.acuity.visualisations.rawdatamodel.service.timeline.data;

import com.acuity.visualisations.rawdatamodel.vo.HasStartEndDate;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * It represents list of events happes in the same period of time.
 */
@Data
public class TimelineBucket<T extends HasStartEndDate> implements HasStartEndDate {
    private Date startDate;
    private Date endDate;
    private List<T> items = new ArrayList<>();
    private boolean ongoing;

    public TimelineBucket(Date startDate) {
        this.startDate = startDate;
    }

    public void addItems(Collection<T> items) {
        this.items.addAll(items);
    }
}
