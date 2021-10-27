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
import com.acuity.visualisations.rawdatamodel.util.BaselineUtil;
import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.util.Constants;
import com.acuity.visualisations.rawdatamodel.vo.HasSubject;
import com.acuity.visualisations.rawdatamodel.vo.LabRaw;
import com.acuity.visualisations.rawdatamodel.vo.RenalRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Lab;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Renal;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Vital;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.acuity.visualisations.rawdatamodel.util.Constants.ROUNDING_PRECISION;

@Component
public class RenalDatasetsDataProvider extends SubjectAwareDatasetsDataProvider<RenalRaw, Renal> {

    @Autowired
    private LabDatasetsDataProvider labDatasetsDataProvider;
    @Autowired
    private VitalDatasetsDataProvider vitalDatasetsDataProvider;

    private static final List<String> RENAL_LABCODES_DIRECT = Arrays.asList(
            "creatinine clearance",
            "creatinine clearance, calculated",
            "creatinine clearance, calculated, corr.",
            "clearance, calculated, corr.",
            "creatinine clearance, predicted mdrd",
            "creatinine clearance (ml/min)",
            "creatinine clearance (calculated) (ml/min)",
            "glomerular filtration rate (51cr-edta)",
            "glomerular filtration rate (amino acids)",
            "glomerular filtration rate (brã–chner-mortensen)",
            "glomerular filtration rate (cockcroft)",
            "glomerular filtration rate (cystatin c)",
            "glomerular filtration rate (iohexol)",
            "glomerular filtration rate (mdrd)",
            "glomerular filtration rate (schwartz)",
            "glomerular filtration rate (unspecified)",
            "glomerular filtration rate (unspecified), african american");

    private static final List<String> RENAL_LABCODES_FOR_CALCULATION = Arrays.asList(
            "creatinine",
            "creatinine (umol/l)",
            "creatinine (mg/dl)",
            "creatinine (24 h conc.)",
            "creatinine (24 h)",
            "creatinine (in units)",
            "creatinine (per minute)",
            "creatinine (random)",
            "creatinine (random)*"
    );

    private static final List<String> RENAL_NORMALIZED_LABCODES_DIRECT = Arrays.asList("c25747", "creatclr");
    private static final List<String> RENAL_NORMALIZED_LABCODES_FOR_CALCULATION = Collections.singletonList("c64547");

    private static final List<String> RENAL_ORIGINAL_LABCODES_DIRECT = Arrays.asList("01109", "01211", "54211", "54620", "02928");
    private static final List<String> RENAL_NOT_NORMALIZED_LABCODES_FOR_CALCULATION = Arrays.asList("01108", "02108", "55108");

    private static final String CALCULATED_RENAL_LABCODE_PREFIX = "ACUITY Calculated CrCl";

    private static final Double MIN_LAB_VALUE = 0.0001;

    private static Collection<Lab> getFilteredLabs(Collection<Lab> labs) {
        return labs.stream()
                .filter(labRaw -> {
                    String labCodeInLowerCase = labRaw.getEvent().getLabCode() != null
                            ? labRaw.getEvent().getLabCode().toLowerCase() : "";
                    String normalizedLabCodeInLowerCase = labRaw.getEvent().getNormalizedLabCode() != null
                            ? labRaw.getEvent().getNormalizedLabCode().toLowerCase() : "";
                    String notNormalizedLabCodeInLowerCase = labRaw.getEvent().getOriginalLabCode() != null
                            ? labRaw.getEvent().getOriginalLabCode().toLowerCase() : "";
                    return (RENAL_LABCODES_DIRECT.contains(labCodeInLowerCase)
                            || RENAL_LABCODES_FOR_CALCULATION.contains(labCodeInLowerCase)
                            || RENAL_NORMALIZED_LABCODES_DIRECT.contains(normalizedLabCodeInLowerCase)
                            || RENAL_NORMALIZED_LABCODES_FOR_CALCULATION.contains(normalizedLabCodeInLowerCase)
                            || RENAL_ORIGINAL_LABCODES_DIRECT.contains(notNormalizedLabCodeInLowerCase)
                            || RENAL_NOT_NORMALIZED_LABCODES_FOR_CALCULATION.contains(notNormalizedLabCodeInLowerCase));
                })
                .filter(labRaw -> labRaw.getMeasurementTimePoint() != null)
                .collect(Collectors.toList());
    }

    @Override
    protected Collection<RenalRaw> getData(Dataset dataset) {
        Datasets datasets = new Datasets(dataset);
        return dataProvider.getData(RenalRaw.class, dataset, (Dataset ds) -> {
            Collection<Lab> labs = getFilteredLabs(labDatasetsDataProvider.loadData(datasets));
            Map<Subject, List<Vital>> vitalsGroupedBySubject = getWeightMeasurementBySubjects(datasets);
            List<RenalRaw> events = labs.stream().flatMap(lab -> getMethodsForCreatinineClearanceCalculation(lab.getEvent())
                    .map(method -> toRenalRaw(datasets, lab,
                            calculateWeight(lab, vitalsGroupedBySubject.get(lab.getSubject())),
                            method))
            ).filter(renal -> renal.getResultValue() != null).collect(Collectors.toList());

        final Map<String, Subject> subjects = getPopulationDatasetsDataProvider().loadData(datasets)
                .stream().collect(Collectors.toMap(Subject::getSubjectId, s -> s));

        //we need to recalculate baselines
        return BaselineUtil.defineBaselinesForEvents(events, e -> {
                    Subject subject = subjects.get(e.getSubjectId());
                    return subject == null ? null
                            : RenalGroupingKey.builder()
                            .subject(subject)
                            .testName(e.getLabCode())
                            .build();
                },
                (e, b) -> e.getBaselineValue() == null ? e.toBuilder()
                        .baselineValue(b.getResultValue())
                        .baselineFlag(Objects.equals(b, e) ? Constants.BASELINE_FLAG_YES : Constants.BASELINE_FLAG_NO)
                        .build() : e);
        });
    }

    @Override
    protected Class<RenalRaw> rawDataClass() {
        return RenalRaw.class;
    }

    @Override
    protected Renal getWrapperInstance(RenalRaw event, Subject subject) {
        return new Renal(event, subject);
    }


    @Builder
    @AllArgsConstructor
    @EqualsAndHashCode
    static class RenalGroupingKey implements HasSubject {
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

    private Stream<CrClCalculateMethods> getMethodsForCreatinineClearanceCalculation(LabRaw labRaw) {
        String labCodeInLowerCase = labRaw.getLabCode() != null
                ? labRaw.getLabCode().toLowerCase() : "";
        String normalizedLabCodeInLowerCase = labRaw.getNormalizedLabCode() != null
                ? labRaw.getNormalizedLabCode().toLowerCase() : "";
        String notNormalizedLabCodeInLowerCase = labRaw.getOriginalLabCode() != null
                ? labRaw.getOriginalLabCode().toLowerCase() : "";
        if (RENAL_LABCODES_DIRECT.contains(labCodeInLowerCase)
                || RENAL_ORIGINAL_LABCODES_DIRECT.contains(notNormalizedLabCodeInLowerCase)
                || RENAL_NORMALIZED_LABCODES_DIRECT.contains(normalizedLabCodeInLowerCase)) {
            return Stream.of(CrClCalculateMethods.DIRECT);
        }
        if ((RENAL_LABCODES_FOR_CALCULATION.contains(labCodeInLowerCase)
                || RENAL_NOT_NORMALIZED_LABCODES_FOR_CALCULATION.contains(notNormalizedLabCodeInLowerCase)
                || RENAL_NORMALIZED_LABCODES_FOR_CALCULATION.contains(normalizedLabCodeInLowerCase))
                && labRaw.getResultValue() != null) {
            return Stream.of(CrClCalculateMethods.CG, CrClCalculateMethods.EGFR);
        }
        return Stream.empty();
    }

    private RenalRaw toRenalRaw(Datasets datasets, Lab lab, Double weight, CrClCalculateMethods method) {
        RenalRaw.RenalRawBuilder renalRawBuilder = RenalRaw.builder()
                .id((method.equals(CrClCalculateMethods.DIRECT)) ? lab.getId() : UUID.randomUUID().toString())
                .subjectId(lab.getSubjectId())
                .analysisVisit(lab.getEvent().getAnalysisVisit())
                .labCode(method.equals(CrClCalculateMethods.DIRECT) ? lab.getEvent().getLabCode() : method.name)
                .method(method)
                .value(method.calculateValue.apply(RenalCalculationParameters.builder()
                        .lab(lab)
                        .value(lab.getResultValue())
                        .datasetType(Column.DatasetType.fromDatasets(datasets))
                        .weight(weight).build()))
                .unit(method.equals(CrClCalculateMethods.DIRECT) ? lab.getEvent().getUnit() : "mL/min")
                .refHigh(method.equals(CrClCalculateMethods.DIRECT) ? lab.getRefHigh() : null)
                .refLow(method.equals(CrClCalculateMethods.DIRECT) ? lab.getRefLow() : null)
                .visitNumber(lab.getEvent().getVisitNumber())
                .visitDescription(lab.getEvent().getVisitDescription())
                .measurementTimePoint(lab.getMeasurementTimePoint())
                .studyPeriods(lab.getEvent().getStudyPeriods())
                .calcChangeFromBaselineIfNull(lab.getCalcChangeFromBaselineIfNull())
                .calcDaysSinceFirstDoseIfNull(lab.getEvent().getCalcDaysSinceFirstDoseIfNull());

        return renalRawBuilder.build();
    }

    private Map<Subject, List<Vital>> getWeightMeasurementBySubjects(Datasets datasets) {
        return vitalDatasetsDataProvider.loadData(datasets).stream().filter(vital -> "WEIGHT".equals(
                vital.getEvent().getVitalsMeasurement() != null
                        ? vital.getEvent().getVitalsMeasurement().toUpperCase()
                        : "")).collect(Collectors.groupingBy(Vital::getSubject));

    }

    private Double calculateWeight(Lab lab, Collection<Vital> weightMeasurements) {
        if (CollectionUtils.isEmpty(weightMeasurements) || lab.getMeasurementTimePoint() == null) {
            return null;
        }

        Date minMeasurementDate = weightMeasurements.stream().filter(v -> v.getStartDate() != null)
                .min(Comparator.comparing(vital ->
                        Math.abs(vital.getStartDate().getTime() - lab.getMeasurementTimePoint().getTime())))
                .map(Vital::getStartDate).orElse(null);

        return minMeasurementDate == null ? null : weightMeasurements.stream()
                .filter(vital -> minMeasurementDate.equals(vital.getStartDate()))
                .collect(Collectors.averagingDouble(Vital::getResultValue));
    }

    @Getter
    public enum CrClCalculateMethods {
        EGFR(CALCULATED_RENAL_LABCODE_PREFIX + ", eGFR", true, generateMethodForCalculateResultValue(
                (parameters) -> 186 * Math.pow(parameters.getValue() / 88.4, -1.154)
                        * Math.pow(parameters.getLab().getSubject().getAge(), -0.203)
                        * getSexCoefficient(parameters.getLab().getSubject().getSex(), 1., 0.742)
                        * ("BLACK OR AFRICAN AMERICAN".equals(
                        parameters.getLab().getSubject().getRace() != null
                                ? parameters.getLab().getSubject().getRace().toUpperCase()
                                : "") ? 1.21 : 1),
                (parameters) -> 186 * Math.pow(parameters.getValue(), -1.154)
                        * Math.pow(parameters.getLab().getSubject().getAge(), -0.203)
                        * getSexCoefficient(parameters.getLab().getSubject().getSex(), 1., 0.742)
                        * ("BLACK OR AFRICAN AMERICAN".equals(
                        parameters.getLab().getSubject().getRace() != null
                                ? parameters.getLab().getSubject().getRace().toUpperCase()
                                : "") ? 1.21 : 1)

        )),
        CG(CALCULATED_RENAL_LABCODE_PREFIX + ", C-G", true, generateMethodForCalculateResultValue(
                (parameters) -> parameters.getWeight() != null ? (140 - parameters.getLab().getSubject().getAge())
                        * parameters.getWeight()
                        * getSexCoefficient(parameters.getLab().getSubject().getSex(), 1.23, 1.04)
                        / parameters.getValue() : null,
                (parameters) -> parameters.getWeight() != null ? (140 - parameters.getLab().getSubject().getAge())
                        * parameters.getWeight()
                        * getSexCoefficient(parameters.getLab().getSubject().getSex(), 1., 0.85)
                        / (72 * parameters.getValue()) : null
        )),
        DIRECT("", false, RenalCalculationParameters::getValue);

        private String name;
        private Function<RenalCalculationParameters, Double> calculateValue;
        private boolean calculated;

        CrClCalculateMethods(String name, Boolean calculated, Function<RenalCalculationParameters, Double> calculateValue) {
            this.name = name;
            this.calculated = calculated;
            this.calculateValue = calculateValue;
        }

        private static double getSexCoefficient(String sex, Double maleCoef, Double femaleCoef) {
            if ("Female".equals(sex) || "F".equals(sex)) {
                return femaleCoef;
            }
            if ("Male".equals(sex) || "M".equals(sex)) {
                return maleCoef;
            }
            return 0.;
        }

        private static Boolean isAppropriateSex(String sex, Column.DatasetType datasetType) {
            return (datasetType.equals(Column.DatasetType.ACUITY) && ("Female".equals(sex) || "Male".equals(sex)))
                    || (datasetType.equals(Column.DatasetType.DETECT) && ("M".equals(sex) || "F".equals(sex)));
        }

        private static Function<RenalCalculationParameters, Double> generateMethodForCalculateResultValue(
                Function<RenalCalculationParameters, Double> statementForUmolUnits,
                Function<RenalCalculationParameters, Double> statementForMgUnits) {
            return (parameters) -> {
                Subject subject = parameters.getLab().getSubject();
                LabRaw labRaw = parameters.getLab().getEvent();
                if (isAppropriateSex(subject.getSex(), parameters.getDatasetType())
                        && Math.abs(parameters.getValue()) >= MIN_LAB_VALUE
                        && subject.getAge() != null
                        && subject.getAge() != 0) {
                    String unit = labRaw.getUnit() == null ? "" : labRaw.getUnit().toLowerCase();
                    Double result;
                    switch (unit) {
                        case "umol/l":
                            result = statementForUmolUnits.apply(parameters);
                            break;
                        case "mg/dl":
                            result = statementForMgUnits.apply(parameters);
                            break;
                        default:
                            result = null;
                    }
                    return result != null ? Precision.round(result, ROUNDING_PRECISION) : null;
                } else {
                    return null;
                }
            };
        }
    }

    @Data
    @Builder
    private static class RenalCalculationParameters {
        private Lab lab;
        private Double weight;
        private Double value;
        private Column.DatasetType datasetType;
    }
}
