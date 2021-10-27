/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
