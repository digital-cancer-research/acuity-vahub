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
import com.acuity.visualisations.rawdatamodel.util.BaselineUtil;
import com.acuity.visualisations.rawdatamodel.util.Constants;
import com.acuity.visualisations.rawdatamodel.vo.HasSubject;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.VitalRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Vital;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class VitalDatasetsDataProvider extends SubjectAwareDatasetsRegularDataProvider<VitalRaw, Vital> {

    @Override
    protected Collection<VitalRaw> getData(Dataset ds) {
        Collection<VitalRaw> events = rawDataRepository.getRawData(ds.getId());

        final Map<String, Subject> subjects = getPopulationDatasetsDataProvider().loadData(new Datasets(ds))
                .stream().collect(Collectors.toMap(Subject::getSubjectId, s -> s));

        return BaselineUtil.defineBaselinesForEvents(new ArrayList<>(events), e -> {
                    Subject subject = subjects.get(e.getSubjectId());
                    return subject == null ? null
                            : VitalGroupingKey.builder()
                            .subject(subject)
                            .testName(e.getVitalsMeasurement())
                            .unitName(e.getUnit())
                            .anatomicalLocation(e.getAnatomicalLocation())
                            .physicalPosition(e.getPhysicalPosition())
                            .build();
                },
                (e, b) -> e.toBuilder()
                        .baseline(b.getResultValue())
                        .baselineFlag(Objects.equals(b, e) ? Constants.BASELINE_FLAG_YES : Constants.BASELINE_FLAG_NO)
                        .baselineDate(b.getEventDate())
                        .build());

    }

    @Override
    protected Vital getWrapperInstance(VitalRaw event, Subject subject) {
        return new Vital(event, subject);
    }

    @Override
    protected Class<VitalRaw> rawDataClass() {
        return VitalRaw.class;
    }

    @Data
    @Builder
    @EqualsAndHashCode
    static class VitalGroupingKey implements HasSubject {
        private Subject subject;
        private String testName;
        private String unitName;
        private String anatomicalLocation;
        private String physicalPosition;

        @Override
        public String getSubjectId() {
            return getSubject().getSubjectId();
        }
    }
}
