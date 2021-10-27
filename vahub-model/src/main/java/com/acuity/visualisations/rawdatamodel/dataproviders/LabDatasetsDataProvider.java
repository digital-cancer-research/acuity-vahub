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

import com.acuity.visualisations.rawdatamodel.dao.DeviceRepository;
import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsRegularDataProvider;
import com.acuity.visualisations.rawdatamodel.util.BaselineUtil;
import com.acuity.visualisations.rawdatamodel.util.Constants;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.Device;
import com.acuity.visualisations.rawdatamodel.vo.HasSubject;
import com.acuity.visualisations.rawdatamodel.vo.LabRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Lab;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class LabDatasetsDataProvider extends SubjectAwareDatasetsRegularDataProvider<LabRaw, Lab> {

    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;

    @Override
    protected Collection<LabRaw> getData(Dataset dataset) {
        final Map<String, Subject> subjects = populationDatasetsDataProvider.loadData(new Datasets(dataset))
                .stream().collect(Collectors.toMap(Subject::getSubjectId, s -> s));

        Collection<LabRaw> eventsWithDevice = dataProvider.getData(LabRaw.class, dataset, (Dataset ds) -> {
            final List<LabRaw> rawData = rawDataRepository.getRawData(ds.getId()).stream()
                    .collect(Collectors.groupingBy(LabRaw::getId))
                    .values().stream().map(this::resolveIdConflict)
                    .collect(Collectors.toList());

            final List<Device> devices = getDevices(dataset);
            final Map<String, Device> deviceMap = devices.stream().collect(Collectors.toMap(Device::getId, Function.identity()));
            return rawData.stream().map(labRaw -> {
                final String sourceId = labRaw.getSourceId();
                Subject subject = subjects.get(labRaw.getSubjectId());
                OptionalInt daysSinceFirstDose = DaysUtil.daysBetween(subject.getFirstTreatmentDate(), labRaw.getMeasurementTimePoint());
                return labRaw.toBuilder()
                        .daysSinceFirstDose(daysSinceFirstDose.isPresent() ? daysSinceFirstDose.getAsInt() : null)
                        .device(sourceId == null ? null : deviceMap.get(sourceId)).build();
            }).collect(Collectors.toList());
        });

        return BaselineUtil.defineBaselinesForEvents(eventsWithDevice, e -> {
                    Subject subject = subjects.get(e.getSubjectId());
                    return subject == null ? null
                            : LabGroupingKey.builder()
                            .subject(subject)
                            .testName(e.getLabCode())
                            .build();
                },
                (e, b) -> e.toBuilder()
                        .baseline(b.getResultValue())
                        .baselineFlag(Objects.equals(b, e) ? Constants.BASELINE_FLAG_YES : Constants.BASELINE_FLAG_NO)
                        .build());
    }

    @Override
    protected Lab getWrapperInstance(LabRaw event, Subject subject) {
        return new Lab(event, subject);
    }

    @Override
    protected Class<LabRaw> rawDataClass() {
        return LabRaw.class;
    }

    private List<Device> getDevices(Dataset dataset) {
        return deviceRepository.getRawData(dataset.getId());
    }

    @SuppressWarnings("ConstantConditions")
    private LabRaw resolveIdConflict(List<? extends LabRaw> g) {
        //here we resolve potential problems with multi categories match
        //picking first by alphabetic order
        return g.stream().min((o1, o2) -> Comparator.comparing(LabRaw::getCategory).compare(o1, o2)).get();
    }

    @Builder
    @AllArgsConstructor
    @EqualsAndHashCode
    static class LabGroupingKey implements HasSubject {
        private Subject subject;
        private String testName;

        @Override
        public Subject getSubject() {
            return subject;
        }

        @Override
        public String getSubjectId() {
            return subject.getSubjectId();
        }
    }
}
