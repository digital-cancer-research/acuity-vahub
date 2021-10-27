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

package com.acuity.visualisations.rawdatamodel.filters;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.ToString;
import org.apache.http.MethodNotSupportedException;

import java.util.Collection;

import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Radiotherapy.RADIOTHERAPY_LABEL;

/**
 * Combined chemo- and radiotherapy filter class.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class TherapyFilters extends Filters {

    @Getter
    @Setter
    private Integer matchedItemsCount = 0;

    protected SetFilter<String> chemoTherapyStatus = new SetFilter<>(); // chemo
    protected SetFilter<String> radioTherapyStatus = new SetFilter<>(); // radio
    protected SetFilter<String> therapyDescription = new SetFilter<>(); // chemo + radio
    protected SetFilter<String> chemotherapyClass = new SetFilter<>(); // chemo
    protected SetFilter<String> chemotherapyBestResponse = new SetFilter<>(); // chemo
    protected SetFilter<String> reasonForChemotherapyFailure = new SetFilter<>(); // chemo
    protected RangeFilter<Double> radiationDose = new RangeFilter<>(); // radio
    protected RangeFilter<Integer> numberOfChemotherapyCycles = new RangeFilter<>(); // chemo
    protected DateRangeFilter diagnosisDate = new DateRangeFilter(); // pathology
    protected RangeFilter<Integer> daysFromDiagnosisDate = new RangeFilter<>(); // pathology
    protected DateRangeFilter recentProgressionDate = new DateRangeFilter(); // disease extent

    @JsonIgnore
    public static TherapyFilters empty() {
        return new TherapyFilters();
    }

    public static TherapyFilters fromFilters(ChemotherapyFilters chemotherapyFilters, RadiotherapyFilters radiotherapyFilters,
                                             SubjectExtFilters subjectExtFilters) {

        TherapyFilters filters = TherapyFilters.empty();

        filters.radioTherapyStatus = radiotherapyFilters.therapyStatus;
        filters.chemoTherapyStatus = chemotherapyFilters.therapyStatus;

        filters.therapyDescription = chemotherapyFilters.preferredMed;
        if (radiotherapyFilters.isRadiotherapyEnabled()) {
            filters.therapyDescription.completeWithValue(RADIOTHERAPY_LABEL);
        }

        filters.chemotherapyClass = chemotherapyFilters.therapyClass;
        filters.radiationDose = radiotherapyFilters.radiationDose;

        filters.reasonForChemotherapyFailure = chemotherapyFilters.reasonForChemotherapyFailure;
        filters.chemotherapyBestResponse = chemotherapyFilters.chemotherapyBestResponse;
        filters.numberOfChemotherapyCycles = chemotherapyFilters.numberOfChemotherapyCycles;

        filters.diagnosisDate = subjectExtFilters.diagnosisDate;
        filters.daysFromDiagnosisDate = subjectExtFilters.daysFromDiagnosisDate;
        filters.recentProgressionDate = subjectExtFilters.recentProgressionDate;

        return filters;
    }

    public ChemotherapyFilters getChemotherapyFilters() {
        ChemotherapyFilters chemotherapyFilters = ChemotherapyFilters.empty();
        chemotherapyFilters.therapyStatus = chemoTherapyStatus;
        chemotherapyFilters.preferredMed = therapyDescription;
        chemotherapyFilters.therapyClass = chemotherapyClass;
        chemotherapyFilters.reasonForChemotherapyFailure = reasonForChemotherapyFailure;
        chemotherapyFilters.chemotherapyBestResponse = chemotherapyBestResponse;
        chemotherapyFilters.numberOfChemotherapyCycles = numberOfChemotherapyCycles;
        return chemotherapyFilters;
    }

    public RadiotherapyFilters getRadiotherapyFilters() {
        RadiotherapyFilters radiotherapyFilters = RadiotherapyFilters.empty();
        radiotherapyFilters.therapyStatus = radioTherapyStatus;
        radiotherapyFilters.radiationDose = radiationDose;
        if ((!therapyDescription.getValues().isEmpty() || (therapyDescription.getValues().isEmpty() && therapyDescription.getIncludeEmptyValues()))
                && !therapyDescription.getValues().contains(RADIOTHERAPY_LABEL)) {
            radiotherapyFilters.setRadiotherapyEnabled(false);
        }
        return radiotherapyFilters;
    }

    public SubjectExtFilters getSubjectExtFilters() {
        SubjectExtFilters subjectExtFilters = SubjectExtFilters.empty();
        subjectExtFilters.diagnosisDate = diagnosisDate;
        subjectExtFilters.daysFromDiagnosisDate = daysFromDiagnosisDate;
        subjectExtFilters.recentProgressionDate = recentProgressionDate;
        return subjectExtFilters;
    }

    @Override
    @SneakyThrows
    public Query getQuery(Collection subjectIds) {
        throw new MethodNotSupportedException("Not applicable. Call chemo- and radiotherapy queries separately.");
    }
}
