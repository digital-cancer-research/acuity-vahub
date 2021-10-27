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

package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsRegularDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.ExposureRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.exposure.Cycle;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exposure;
import com.acuity.va.security.acl.domain.Dataset;
import org.apache.commons.math3.util.Precision;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.util.Constants.MINUTES_IN_HOUR;

@Component
public class ExposureDatasetsDataProvider extends SubjectAwareDatasetsRegularDataProvider<ExposureRaw, Exposure> {

    @Override
    protected Collection<ExposureRaw> getData(Dataset dataset) {
        return dataProvider.getData(ExposureRaw.class, dataset, ds -> {
            Collection<ExposureRaw> rawData = rawDataRepository.getRawData(ds.getId());
            boolean isNotAllDrugDatesEmpty = rawData.stream().map(ExposureRaw::getDrugAdministrationDate).anyMatch(Objects::nonNull);

            return rawData.stream()
                    .map(e -> e.toBuilder().cycle(new Cycle(e.getTreatmentCycle(), e.getAnalyte(), e.getVisitNumber(),
                            e.getDrugAdministrationDate(), isNotAllDrugDatesEmpty))
                            .timeFromAdministration(getTimeFromAdministration(e.getNominalHour(), e.getNominalMinute()))
                            .build()).collect(Collectors.toList());
        });
    }

    @Override
    protected Exposure getWrapperInstance(ExposureRaw event, Subject subject) {
        return new Exposure(event, subject);
    }

    @Override
    protected Class<ExposureRaw> rawDataClass() {
        return ExposureRaw.class;
    }

    private Double getTimeFromAdministration(Double hours, Integer minutes) {
        hours = hours == null ? 0 : hours;
        minutes = minutes == null ? 0 : minutes;
        return hours + Precision.round((double) minutes / MINUTES_IN_HOUR, 2);
    }
}
