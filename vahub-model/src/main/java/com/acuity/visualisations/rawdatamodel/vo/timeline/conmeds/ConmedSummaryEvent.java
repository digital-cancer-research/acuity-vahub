package com.acuity.visualisations.rawdatamodel.vo.timeline.conmeds;

import com.acuity.visualisations.rawdatamodel.vo.timeline.EventInterval;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ConmedSummaryEvent extends EventInterval implements Serializable {
    protected List<ConmedSummary> conmeds;

    public int getNumberOfConmeds() {
        return conmeds == null ? 0 : conmeds.size();
    }
}
