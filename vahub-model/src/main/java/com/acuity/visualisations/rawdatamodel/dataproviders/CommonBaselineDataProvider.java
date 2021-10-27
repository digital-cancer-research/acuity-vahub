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

import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.TargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.EventWrapper;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.TargetLesion;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public interface CommonBaselineDataProvider {

    default Map<String, Date> defineBaselineDatePerSubject(Map<String, List<TargetLesionRaw>> tlBySubject, Collection<Subject> subjects) {
        Map<String, Date> subjectFirstDoseDate = subjects.stream()
                .collect(Collectors.toMap(Subject::getSubjectId, Subject::getBaselineDate));

        // cannot merge null values to HashMap using Collectors.toMap()
        // see https://stackoverflow.com/questions/24630963/java-8-nullpointerexception-in-collectors-tomap
        return tlBySubject.entrySet().stream()
                .collect(HashMap::new, (m, v) -> {
                    List<Date> lesionDates = v.getValue().stream()
                            .map(TargetLesionRaw::getLesionDate)
                            .distinct()
                            .collect(Collectors.toList());
                    Date value = getBaselineDate(lesionDates, subjectFirstDoseDate.get(v.getKey())).orElse(null);
                    m.put(v.getKey(), value);
                }, HashMap::putAll);
    }

    /**
     * Method splits set of dates in to dates before and after {@code firstDoseDate} and picks closest to it. First,
     * closest searched among the dates before. If nothing found, the after dates searched.
     *
     * @param lesionDates   dates of visit
     * @param firstDoseDate date of first dose
     * @return
     */
    default Optional<Date> getBaselineDate(List<Date> lesionDates, Date firstDoseDate) {
        if (firstDoseDate == null) {
            return Optional.empty();
        }
        List<Date> beforeFirstDose = new LinkedList<>();
        List<Date> afterFirstDose = new LinkedList<>();
        lesionDates.forEach(visit -> {
            if (firstDoseDate.compareTo(visit) >= 0) {
                beforeFirstDose.add(visit);
            } else {
                afterFirstDose.add(visit);
            }
        });
        Optional<Date> result = beforeFirstDose.stream().max(Date::compareTo);
        if (!result.isPresent()) {
            result = afterFirstDose.stream().min(Date::compareTo);
        }
        return result;
    }

    default Map<String, List<TargetLesionRaw>> groupTLBySubject(Collection<TargetLesion> lesions) {
        return lesions.stream()
                .filter(l -> l.getEvent().getLesionDate() != null)
                .collect(Collectors.groupingBy(TargetLesion::getSubjectId))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, t -> t.getValue().stream().map(EventWrapper::getEvent).collect(Collectors.toList())));
    }
}
