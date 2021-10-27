package com.acuity.visualisations.rawdatamodel.service.proact;

import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.proact.ProactPatient;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Used to get patient data during the synchronisation between PROACT and ACUITY.
 */
@Service
public class ProactPopulationService {

    @Autowired
    private PopulationDatasetsDataProvider dataProvider;

    /**
     * Gets patients by datasets.
     *
     * @param datasets dataset list
     * @return list of patients
     */
    public List<ProactPatient> getProactPatientList(Datasets datasets) {
        return dataProvider.loadData(datasets).stream()
                .map(subj -> ProactPatient.builder()
                        .patientId(subj.getSubjectId())
                        .subjectCode(subj.getSubjectCode())
                        .race(subj.getRace())
                        .sex(subj.getSex())
                        .birthDate(subj.getDateOfBirth())
                        .firstVisitDate(subj.getEnrollVisitDate())
                        .firstDoseDate(subj.getFirstTreatmentDate())
                        .country(subj.getCountry())
                        .centre(subj.getCenterNumber())
                        .build()
                ).collect(Collectors.toList());
    }

}
