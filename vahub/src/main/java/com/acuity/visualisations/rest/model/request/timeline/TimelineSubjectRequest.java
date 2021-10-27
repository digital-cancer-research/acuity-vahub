package com.acuity.visualisations.rest.model.request.timeline;

import com.acuity.visualisations.common.vo.DayZeroType;
import com.acuity.visualisations.rawdatamodel.axes.TAxes;
import com.acuity.visualisations.rawdatamodel.filters.AeFilters;
import com.acuity.visualisations.rawdatamodel.filters.CardiacFilters;
import com.acuity.visualisations.rawdatamodel.filters.ConmedFilters;
import com.acuity.visualisations.rawdatamodel.filters.DrugDoseFilters;
import com.acuity.visualisations.rawdatamodel.filters.ExacerbationFilters;
import com.acuity.visualisations.rawdatamodel.filters.LabFilters;
import com.acuity.visualisations.rawdatamodel.filters.LungFunctionFilters;
import com.acuity.visualisations.rawdatamodel.filters.PatientDataFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.VitalFilters;
import com.acuity.visualisations.rawdatamodel.service.timeline.data.TimelineTrack;
import com.acuity.va.security.acl.domain.DatasetsRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class TimelineSubjectRequest extends DatasetsRequest {

    private PopulationFilters populationFilters;
    private AeFilters aesFilters;
    private ConmedFilters conmedsFilters;
    private DrugDoseFilters doseFilters;
    private CardiacFilters cardiacFilters;
    private LabFilters labsFilters;
    private LungFunctionFilters lungFunctionFilters;
    private ExacerbationFilters exacerbationsFilters;
    private VitalFilters vitalsFilters;
    private PatientDataFilters patientDataFilters;

    private List<TimelineTrack> visibleTracks;
    private TAxes<DayZeroType> dayZero;
}
