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

package com.acuity.visualisations.rawdatamodel.vo;

import java.util.Date;


public interface HasSubject extends HasSubjectId {

    Subject getSubject();

    String getSubjectId();

    default String getSubjectCode() {
        return getSubject().getSubjectCode();
    }

    default String getClinicalStudyName() {
        return getSubject().getClinicalStudyName();
    }

    default String getClinicalStudyCode() {
        return getSubject().getClinicalStudyCode();
    }

    default String getDatasetCode() {
        return getSubject().getDatasetCode();
    }

    default String getDatasetName() {
        return getSubject().getDatasetName();
    }

    default String getStudyPart() {
        return getSubject().getStudyPart();
    }

    default Date getDateOfFirstDose() {
        return getSubject().getFirstTreatmentDate();
    }

    default Date getDateOfFirstDoseOfDrug(String drug) {
        return drug == null || drug.length() == 0 ? getSubject().getFirstTreatmentDate() : getSubject().getDrugFirstDoseDate().get(drug);
    }
}
