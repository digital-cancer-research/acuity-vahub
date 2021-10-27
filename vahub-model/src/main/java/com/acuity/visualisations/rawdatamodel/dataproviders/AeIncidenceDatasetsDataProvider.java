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

import com.acuity.visualisations.rawdatamodel.dao.AeRepository;
import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.AeRaw;
import com.acuity.visualisations.rawdatamodel.vo.AeSeverityEventCategoryValue;
import com.acuity.visualisations.rawdatamodel.vo.AeSeverityRaw;
import com.acuity.visualisations.rawdatamodel.vo.EventCategoryValue;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae;
import com.acuity.va.security.acl.domain.Dataset;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Component
public class AeIncidenceDatasetsDataProvider extends SubjectAwareDatasetsDataProvider<AeRaw, Ae> {

    @Autowired
    private AeRepository aeRepository;

    @Getter
    private static class AeMapFieldsHolder {
        private final Map<String, String> drugsCausality = new HashMap<>();
    }

    @Getter
    private static class AeSeverityMapFieldsHolder {
        private final Map<String, String> drugsActionTaken = new HashMap<>();
    }

    @Override
    protected Ae getWrapperInstance(AeRaw event, Subject subject) {
        return new Ae(event, subject);
    }

    @Override
    protected Class<AeRaw> rawDataClass() {
        return AeRaw.class;
    }

    @Override
    protected Collection<AeRaw> getData(Dataset dataset) {
        return dataProvider.getData(AeRaw.class, dataset, ds -> {
                    List<AeSeverityEventCategoryValue> actionTaken = aeRepository.getDistinctDrugsActionTaken(ds.getId());
                    List<EventCategoryValue> causality = aeRepository.getDistinctDrugsCausality(ds.getId());

                    Map<String, AeMapFieldsHolder> aeWithMaps = new HashMap<>();
                    Map<String, AeSeverityMapFieldsHolder> aeWithSeverityMaps = new HashMap<>();

                    actionTaken.forEach(aeActionTaken -> populateEventActionTaken(aeWithSeverityMaps, aeActionTaken));
                    causality.forEach(aeCausality -> populateEventCausality(aeWithMaps, aeCausality));

                    return enrichRawData(aeRepository.getRawData(ds.getId()), aeWithMaps, aeWithSeverityMaps);
                }
        );
    }

    private List<AeRaw> enrichRawData(Collection<AeRaw> rawData,
                                      Map<String, AeMapFieldsHolder> aeWithMaps,
                                      Map<String, AeSeverityMapFieldsHolder> aeWithSeverityMaps) {
        return rawData.stream()
                .map(ae -> {
                    AeRaw.AeRawBuilder builder = ae.toBuilder();
                    AeMapFieldsHolder eventAeRaw = aeWithMaps.get(ae.getId());
                    setMaps(builder, eventAeRaw);

                    List<AeSeverityRaw> aesSevWithDrugActionTaken = ae.getAeSeverities().stream()
                            .map(sev -> {
                                AeSeverityRaw.AeSeverityRawBuilder sevBuilder = sev.toBuilder();
                                setSeverityMaps(sevBuilder, aeWithSeverityMaps.get(sev.getId()));

                                return sevBuilder.build();
                            }).collect(toList());

                    builder.aeSeverities(aesSevWithDrugActionTaken);

                    return builder.build();
                }).collect(Collectors.toList());
    }

    private void setMaps(AeRaw.AeRawBuilder builder, AeMapFieldsHolder aeRaw) {
        if (aeRaw != null) {
            builder.drugsCausality(aeRaw.getDrugsCausality());
        } else {
            builder.drugsCausality(new HashMap<>());
        }
    }

    private void setSeverityMaps(AeSeverityRaw.AeSeverityRawBuilder builder, AeSeverityMapFieldsHolder aeSeverityRaw) {
        if (aeSeverityRaw != null) {
            builder.drugsActionTaken(aeSeverityRaw.getDrugsActionTaken());
        } else {
            builder.drugsActionTaken(null);
        }
    }

    private void populateEventActionTaken(Map<String, AeSeverityMapFieldsHolder> aeWithActionTakenMap, AeSeverityEventCategoryValue aeCategoryValue) {
        aeWithActionTakenMap.putIfAbsent(aeCategoryValue.getSeverityId(), new AeSeverityMapFieldsHolder());
        AeSeverityMapFieldsHolder aeSeverityMapFieldsHolder = aeWithActionTakenMap.get(aeCategoryValue.getSeverityId());

        String category = aeCategoryValue.getCategory() == null ? "" : aeCategoryValue.getCategory();
        aeSeverityMapFieldsHolder.getDrugsActionTaken().put(category, aeCategoryValue.getValue());
    }

    private void populateEventCausality(Map<String, AeMapFieldsHolder> aeWithCausalityMap, EventCategoryValue aeCategoryValue) {
        aeWithCausalityMap.putIfAbsent(aeCategoryValue.getEventId(), new AeMapFieldsHolder());
        AeMapFieldsHolder aeMapFieldsHolder = aeWithCausalityMap.get(aeCategoryValue.getEventId());

        String category = aeCategoryValue.getCategory() == null ? "" : aeCategoryValue.getCategory();
        aeMapFieldsHolder.getDrugsCausality().put(category, aeCategoryValue.getValue());
    }
}
