package com.acuity.visualisations.rest.model.request.patient.data;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.PatientDataGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.PatientData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class TimelinePatientDataRequest extends PatientDataRequest {
    @NotNull
    private Map<ChartGroupByOptions.ChartGroupBySetting, ChartGroupByOptions.GroupByOptionAndParams<PatientData, PatientDataGroupByOptions>> options;
    private Set<String> subjectIds;
}
