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
import com.acuity.visualisations.rawdatamodel.vo.HasSubject;
import com.acuity.visualisations.rawdatamodel.vo.LungFunctionRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LungFunction;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.util.Constants.BASELINE_FLAG_NO;
import static com.acuity.visualisations.rawdatamodel.util.Constants.BASELINE_FLAG_YES;

@Component
@RequiredArgsConstructor
public class LungFunctionDatasetsDataProvider
        extends SubjectAwareDatasetsRegularDataProvider<LungFunctionRaw, LungFunction> {

    private final PopulationDatasetsDataProvider populationDatasetsDataProvider;

    @Override
    protected Collection<LungFunctionRaw> getData(Dataset dataset) {
        List<LungFunctionRaw> events = new ArrayList<>(rawDataRepository.getRawData(dataset.getId()));

        final Map<String, Subject> subjects = populationDatasetsDataProvider.loadData(new Datasets(dataset))
                .stream().collect(Collectors.toMap(Subject::getSubjectId, s -> s));

        return BaselineUtil.defineBaselinesForEvents(events, e -> {
                    Subject subject = subjects.get(e.getSubjectId());
                    return subject == null ? null
                            : LungFunctionGroupingKey.builder()
                            .subject(subject)
                            .measurementName(e.getMeasurementNameRaw())
                            .protocolScheduleTimepoint(e.getProtocolScheduleTimepoint())
                            .build();
                },
                (e, b) -> e.toBuilder()
                        .baselineValue(b.getResultValue())
                        .baselineFlag(Objects.equals(b, e) ? BASELINE_FLAG_YES : BASELINE_FLAG_NO)
                        .baselineDate(b.getEventDate())
                        .build());
    }

    @Override
    protected Class<LungFunctionRaw> rawDataClass() {
        return LungFunctionRaw.class;
    }

    @Override
    protected LungFunction getWrapperInstance(LungFunctionRaw event, Subject subject) {
        return new LungFunction(event, subject);
    }

    @Data
    @Builder
    @EqualsAndHashCode
    static class LungFunctionGroupingKey implements HasSubject {
        private Subject subject;
        private String measurementName;
        private String protocolScheduleTimepoint;

        @Override
        public String getSubjectId() {
            return getSubject().getSubjectId();
        }
    }
}
