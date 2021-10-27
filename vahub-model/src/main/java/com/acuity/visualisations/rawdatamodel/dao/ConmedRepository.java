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

package com.acuity.visualisations.rawdatamodel.dao;

import com.acuity.visualisations.common.lookup.AcuityRepository;
import com.acuity.visualisations.rawdatamodel.dao.api.RawDataRepository;
import com.acuity.visualisations.rawdatamodel.vo.ConmedRaw;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.Set;

@AcuityRepository
public interface ConmedRepository extends RawDataRepository<ConmedRaw> {
    @Select("SELECT DISTINCT cms_id         AS event_id, "
            + "  cms_pat_id                 AS subject_id, "

            + "  med_drug_name              AS medication_name, "
            + "  cms_atc_code               AS atc_code, "
            + "  cms_dose                   AS dose, "
            + "  cms_dose_unit              AS dose_units, "
            + "  cms_frequency              AS dose_frequency, "
            + "  cms_start_date             AS start_date, "
            + "  cms_end_date               AS end_date, "
            + "  cms_atc_code_text          AS atc_code_text, "
            + "  cms_reason                 AS treatment_reason, "

            + "  cms_dose_total             AS dose_total, "
            + "  cms_dose_unit_other        AS dose_units_other, "
            + "  cms_frequency_other        AS dose_frequency_other, "
            + "  cms_route                  AS route, "
            + "  cms_therapy_reason         AS therapy_reason, "
            + "  cms_reason_other           AS therapy_reason_other, "
            + "  cms_proph_spec_other       AS other_prophylaxis_spec, "
            + "  med_drug_parent            AS medication_class, "
            + "  cms_inf_body_sys           AS infection, "
            + "  cms_inf_body_sys_other     AS infection_other, "
            + "  cms_active_ingr_1          AS active_ingr_1, "
            + "  cms_active_ingr_2          AS active_ingr_2, "
            + "  cms_reason_stop            AS reason_for_stop,"
            + "  cms_reason_stop_other      AS reason_for_stop_other, "
            + "  cms_ae_num                 AS ae_no, "
            + "  event_type.evt_pt          AS ae_pt "

            + "FROM result_conmed_schedule con "
            + "INNER JOIN result_patient "
            + "ON cms_pat_id = pat_id "
            // IMPORTANT TODO !!! refactor this to avoid joining AEs on SQL query stage
            // this will cause lots of problems, all cross-entity relations best to be built in Java code
            // Also, sometimes the relation may be ambiguous and this approach is causing event duplication
            // I have to change result type to set to prevent events duplication
            + "LEFT JOIN result_ae "
            + "ON ae_number = cms_ae_num AND ae_pat_id = pat_id "
            + "LEFT JOIN result_event_type event_type "
            + "ON ae_evt_id = evt_id "
            + "INNER JOIN result_medicine "
            + "ON med_id = cms_med_id "
            + "INNER JOIN result_study "
            + "ON pat_std_id = std_id "
            + "INNER JOIN map_study_rule "
            + "ON msr_study_code = std_name "
            + "WHERE msr_id      = #{datasetId}")
    @Results({
            @Result(property = "id", column = "event_id"),
            @Result(property = "subjectId", column = "subject_id"),

            @Result(property = "medicationName", column = "medication_name"),
            @Result(property = "atcCode", column = "atc_code"),
            @Result(property = "dose", column = "dose"),
            @Result(property = "doseUnits", column = "dose_units"),
            @Result(property = "doseFrequency", column = "dose_frequency"),
            @Result(property = "startDate", column = "start_date"),
            @Result(property = "endDate", column = "end_date"),
            @Result(property = "atcText", column = "atc_code_text"),
            @Result(property = "treatmentReason", column = "treatment_reason"),

            @Result(property = "doseTotal", column = "dose_total"),
            @Result(property = "doseUnitsOther", column = "dose_units_other"),
            @Result(property = "frequencyOther", column = "dose_frequency_other"),
            @Result(property = "route", column = "route"),
            @Result(property = "therapyReason", column = "therapy_reason"),
            @Result(property = "therapyReasonOther", column = "therapy_reason_other"),
            @Result(property = "otherProphylaxisSpec", column = "other_prophylaxis_spec"),
            @Result(property = "medicationClass", column = "medication_class"),
            @Result(property = "infectionBodySystem", column = "infection"),
            @Result(property = "infectionBodySystemOther", column = "infection_other"),
            @Result(property = "activeIngredient1", column = "active_ingr_1"),
            @Result(property = "activeIngredient2", column = "active_ingr_2"),
            @Result(property = "reasonForTreatmentStop", column = "reason_for_stop"),
            @Result(property = "reasonForTreatmentStopOther", column = "reason_for_stop_other"),
            @Result(property = "aeNum", column = "ae_no"),
            @Result(property = "aePt", column = "ae_pt")
    })
    @Options(fetchSize = 5000)
    Set<ConmedRaw> getRawData(@Param("datasetId") long datasetId);
}

