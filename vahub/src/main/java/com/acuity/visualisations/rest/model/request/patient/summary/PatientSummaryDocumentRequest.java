package com.acuity.visualisations.rest.model.request.patient.summary;

import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rest.model.request.SingleSubjectRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PatientSummaryDocumentRequest extends SingleSubjectRequest<PopulationFilters> {
    private String timeZoneOffset;
}
