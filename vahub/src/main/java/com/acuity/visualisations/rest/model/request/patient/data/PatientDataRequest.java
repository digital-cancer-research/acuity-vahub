package com.acuity.visualisations.rest.model.request.patient.data;

import com.acuity.visualisations.rawdatamodel.filters.PatientDataFilters;
import com.acuity.visualisations.rest.model.request.EventFilterRequestPopulationAware;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PatientDataRequest extends EventFilterRequestPopulationAware<PatientDataFilters> {

    private PatientDataFilters patientDataFilters;

    @Override
    public PatientDataFilters getEventFilters() {
        return patientDataFilters;
    }
}
