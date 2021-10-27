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

import com.acuity.visualisations.rawdatamodel.dao.cardiac.CardiacDecgRawDataRepository;
import com.acuity.visualisations.rawdatamodel.dao.cardiac.CardiacEcgRawDataRepository;
import com.acuity.visualisations.rawdatamodel.dao.cardiac.CardiacLvefRawDataRepository;
import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.util.BaselineUtil;
import com.acuity.visualisations.rawdatamodel.util.Constants;
import com.acuity.visualisations.rawdatamodel.vo.CardiacDecgRaw;
import com.acuity.visualisations.rawdatamodel.vo.CardiacRaw;
import com.acuity.visualisations.rawdatamodel.vo.HasSubject;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Cardiac;
import com.acuity.va.security.acl.domain.Dataset;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;


@Component
@RequiredArgsConstructor
public class CardiacDatasetsDataProvider extends SubjectAwareDatasetsDataProvider<CardiacRaw, Cardiac> {

    private final CardiacLvefRawDataRepository acuityCardiacLvefRepository;

    private final CardiacEcgRawDataRepository acuityCardiacEcgRepository;

    private final CardiacDecgRawDataRepository acuityCardiacDecgRepository;

    private static final String INTP = "INTP";
    private static final String ECG = "ECG";

    @Override
    protected Collection<CardiacRaw> getData(Dataset dataset) {
        Collection<CardiacRaw> events = new ArrayList<>();

        events.addAll(acuityCardiacLvefRepository.getRawData(dataset.getId()));
        events.addAll(acuityCardiacEcgRepository.getRawData(dataset.getId()));
        events.addAll(mergeAcuityDecgRecords(acuityCardiacDecgRepository.getRawData(dataset.getId())));

        final Map<String, Subject> subjects = getPopulationDatasetsDataProvider().getData(dataset)
                .stream().collect(toMap(Subject::getSubjectId, s -> s));

        return BaselineUtil.defineBaselinesForEvents(events, e -> {
                    Subject subject = subjects.get(e.getSubjectId());
                    return subject == null ? null
                            : CardiacGroupingKey.builder()
                            .subject(subject)
                            .testName(e.getMeasurementName())
                            .build();
                },
                (e, b) -> e.toBuilder()
                        .baselineValue(b.getResultValue())
                        .baselineFlag(Objects.equals(b, e) ? Constants.BASELINE_FLAG_YES : Constants.BASELINE_FLAG_NO)
                        .baselineDate(b.getEventDate())
                        .build());
    }

    @Override
    protected Class<CardiacRaw> rawDataClass() {
        return CardiacRaw.class;
    }

    private static List<CardiacRaw> mergeAcuityDecgRecords(List<CardiacDecgRaw> cardiacRaws) {
        return cardiacRaws.stream()
                .collect(groupingBy(decg1 -> CardiacMergeKey.builder()
                        .subjectId(decg1.getSubjectId())
                        .visitNumber(decg1.getVisitNumber())
                        .measurementDate(decg1.getMeasurementTimePoint())
                        .build()))
                .values().stream()
                .flatMap(decgList -> {
                    CardiacDecgRaw interpretation = decgList.stream()
                            .filter(decg -> INTP.equals(decg.getMeasurementName())).findFirst().orElse(null);

                    return decgList.stream()
                            .filter(decg -> !INTP.equals(decg.getMeasurementName()))
                            .filter(decg -> StringUtils.isNumeric(decg.getResultValue()))
                            .map(decg -> composeCardiacRaw(decg, interpretation));
                })
                .collect(toList());
    }

    private static CardiacRaw composeCardiacRaw(CardiacDecgRaw decg, CardiacDecgRaw interpretation) {
        return CardiacRaw.builder()
                .id(decg.getId())
                .subjectId(decg.getSubjectId())
                .measurementCategory(ECG)
                .measurementName(decg.getMeasurementName())
                .measurementTimePoint(decg.getMeasurementTimePoint())
                .visitNumber(decg.getVisitNumber())
                .resultValue(Double.valueOf(decg.getResultValue()))
                .protocolScheduleTimepoint(decg.getProtocolScheduleTimepoint())
                .clinicallySignificant(decg.getClinicallySignificant())
                .method(decg.getMethod())
                .beatGroupNumber(decg.getBeatGroupNumber())
                .beatNumberWithinBeatGroup(decg.getBeatNumberWithinBeatGroup())
                .numberOfBeatsInAverageBeat(decg.getNumberOfBeatsInAverageBeat())
                .beatGroupLengthInSec(decg.getBeatGroupLengthInSec())
                .comment(decg.getComment())
                .ecgEvaluation(interpretation != null && decg.getEcgEvaluation() == null
                        ? interpretation.getEcgEvaluation() : decg.getEcgEvaluation())
                .build();
    }

    @Override
    protected Cardiac getWrapperInstance(CardiacRaw event, Subject subject) {
        return new Cardiac(event, subject);
    }

    @Builder
    @AllArgsConstructor
    @EqualsAndHashCode
    private static class CardiacMergeKey {
        private String subjectId;
        private Double visitNumber;
        private Date measurementDate;
    }

    @Builder
    @AllArgsConstructor
    @EqualsAndHashCode
    static class CardiacGroupingKey implements HasSubject {
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
