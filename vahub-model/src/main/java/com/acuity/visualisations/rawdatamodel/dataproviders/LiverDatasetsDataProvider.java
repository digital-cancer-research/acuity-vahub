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

import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.LabRaw;
import com.acuity.visualisations.rawdatamodel.vo.LiverRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Liver;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class LiverDatasetsDataProvider extends SubjectAwareDatasetsDataProvider<LiverRaw, Liver> {

    @Autowired
    private LabDatasetsDataProvider labDatasetsDataProvider;

    private static Collection<LabRaw> getFilteredLabs(Datasets datasets, Collection<LabRaw> labs) {
        if (datasets.isAcuityType()) {
            return labs.stream()
                    .filter(labRaw -> labRaw.getValue() != null)
                    .filter(labRaw -> {
                                String normalizedLabCode = labRaw.getLabCode().toUpperCase();
                                return ("ASPARTATE AMINOTRANSFERASE".equals(normalizedLabCode)
                                        || "TOTAL BILIRUBIN".equals(normalizedLabCode)
                                        || "ALANINE AMINOTRANSFERASE".equals(normalizedLabCode)
                                        || "ALKALINE PHOSPHATASE".equals(normalizedLabCode)
                                        //This "PHOSPHATASE" added only for compability with SQL solution
                                );
                            }
                    ).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    private static String toNormalizedLabCode(LabRaw lab) {
        String labcode = lab.getLabCode().toUpperCase();
        switch (labcode) {
            case "ASPARTATE AMINOTRANSFERASE":
                return Liver.LiverCode.AST.name();
            case "ALANINE AMINOTRANSFERASE":
                return Liver.LiverCode.ALT.name();
            case "TOTAL BILIRUBIN":
                return Liver.LiverCode.BILI.name();
            case "ALKALINE PHOSPHATASE":
                return Liver.LiverCode.ALP.name();
            default:
                return null;
        }
    }


    @Override
    protected Collection<LiverRaw> getData(Dataset dataset) {
        Collection<LabRaw> labs = labDatasetsDataProvider.getData(dataset);
        Datasets datasets = new Datasets(dataset);

        labs = getFilteredLabs(datasets, labs);

        return labs.stream().map(lab -> LiverRaw.builder()
                .id(lab.getId())
                .subjectId(lab.getSubjectId())
                .labCode(lab.getLabCode())
                .category(lab.getCategory())
                .value(lab.getValue())
                .unit(lab.getUnit())
                .baseline(lab.getBaseline())
                .changeFromBaselineRaw(lab.getChangeFromBaselineRaw())
                .baselineFlag(lab.getBaselineFlag())
                .refHigh(lab.getRefHigh())
                .refLow(lab.getRefLow())
                .measurementTimePoint(lab.getMeasurementTimePoint())
                .visitNumber(lab.getVisitNumber())
                .analysisVisit(lab.getAnalysisVisit())
                .visitDescription(lab.getVisitDescription())
                .comment(lab.getComment())
                .valueDipstick(lab.getValueDipstick())
                .protocolScheduleTimepoint(lab.getProtocolScheduleTimepoint())
                .studyPeriods(lab.getStudyPeriods())
                .daysSinceFirstDose(lab.getDaysSinceFirstDose())
                .calcChangeFromBaselineIfNull(lab.getCalcChangeFromBaselineIfNull())
                .calcDaysSinceFirstDoseIfNull(lab.getCalcDaysSinceFirstDoseIfNull())
                .normalizedLabCode(toNormalizedLabCode(lab))
                .build())
                .collect(Collectors.toList());
    }

    @Override
    protected Class<LiverRaw> rawDataClass() {
        return LiverRaw.class;
    }

    @Override
    protected Liver getWrapperInstance(LiverRaw event, Subject subject) {
        return new Liver(event, subject);
    }
}
