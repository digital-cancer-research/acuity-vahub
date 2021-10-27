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
import com.acuity.visualisations.rawdatamodel.vo.DrugDoseRaw;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@AcuityRepository
public interface DrugDoseRepository extends RawDataRepository<DrugDoseRaw> {
    @Select("SELECT mds_id                      AS event_id, "
            + "  mds_pat_id                     AS subject_id, "
            + "  mds_start_date                 AS start_date, "
            + "  mds_end_date                   AS end_date, "
            + "  mds_drug                       AS drug, "
            + "  mds_dose                       AS dose, "
            + "  mds_action_taken               AS action_taken, "
            + "  mds_reason_for_action_taken    AS reason_for_action_taken, "
            + "  mds_frequency                  AS frequency, "
            + "  mds_dosing_freq_name           AS frequency_name, "
            + "  mds_dosing_freq_rank           AS frequency_rank, "
            + "  mds_dose_unit                  AS dose_unit, "
            + "  mds_period_type                AS period_type, "
            + "  mds_subsequent_period_type     AS subsequent_period_type, "
            + "  mds_study_drug_cat             AS study_drug_cat, "
            + "  mds_total_daily_dose           AS total_daily_dose, "
            + "  mds_planned_dose               AS planned_dose, "
            + "  mds_planned_dose_units         AS planned_dose_units, "
            + "  mds_planned_no_days_trt        AS planned_no_days_trt, "
            + "  mds_formulation                AS formulation, "
            + "  mds_route                      AS route, "
            + "  mds_reason_for_therapy         AS reason_for_therapy, "
            + "  mds_cycle_delayed              AS cycle_delayed, "
            + "  mds_reason_cycle_delayed       AS reason_cycle_delayed, "
            + "  mds_reason_cycle_delayed_oth   AS reason_cycle_delayed_oth, "
            + "  mds_med_code                   AS med_code, "
            + "  mds_med_dictionary_text        AS med_dictionary_text, "
            + "  mds_atc_code                   AS atc_code, "
            + "  mds_atc_dictionary_text        AS atc_dictionary_text, "
            + "  mds_med_group_name             AS med_group_name, "
            + "  mds_drug_pref_name             AS drug_pref_name, "
            + "  mds_comment                    AS main_reason, "
            + "  mds_active_ingredient          AS active_ingredient "
            + "FROM result_trg_med_dos_schedule "
            + "JOIN result_patient "
            + "ON mds_pat_id = pat_id "
            + "JOIN result_study "
            + "ON std_id = pat_std_id "
            + "INNER JOIN map_study_rule "
            + "ON msr_study_code = std_name "
            + "WHERE msr_id      = #{datasetId}")
    @Results(value = {
            @Result(property = "id", column = "event_id"),
            @Result(property = "subjectId", column = "subject_id"),
            @Result(property = "startDate", column = "start_date"),
            @Result(property = "endDate", column = "end_date"),
            @Result(property = "drug", column = "drug"),
            @Result(property = "dose", column = "dose"),
            @Result(property = "actionTaken", column = "action_taken"),
            @Result(property = "reasonForActionTaken", column = "reason_for_action_taken"),
            @Result(property = "frequency", column = "frequency"),
            @Result(property = "frequencyName", column = "frequency_name"),
            @Result(property = "frequencyRank", column = "frequency_rank"),
            @Result(property = "doseUnit", column = "dose_unit"),
            @Result(property = "periodType", column = "period_type"),
            @Result(property = "subsequentPeriodType", column = "subsequent_period_type"),
            @Result(property = "studyDrugCategory", column = "study_drug_cat"),
            @Result(property = "totalDailyDose", column = "total_daily_dose"),
            @Result(property = "plannedDose", column = "planned_dose"),
            @Result(property = "plannedDoseUnits", column = "planned_dose_units"),
            @Result(property = "plannedNoDaysTreatment", column = "planned_no_days_trt"),
            @Result(property = "formulation", column = "formulation"),
            @Result(property = "route", column = "route"),
            @Result(property = "mainReasonForActionTakenSpec", column = "main_reason"),
            @Result(property = "reasonForTherapy", column = "reason_for_therapy"),
            @Result(property = "treatmentCycleDelayed", column = "cycle_delayed"),
            @Result(property = "reasonTreatmentCycleDelayed", column = "reason_cycle_delayed"),
            @Result(property = "reasonTreatmentCycleDelayedOther", column = "reason_cycle_delayed_oth"),
            @Result(property = "medicationCode", column = "med_code"),
            @Result(property = "medicationDictionaryText", column = "med_dictionary_text"),
            @Result(property = "atcCode", column = "atc_code"),
            @Result(property = "atcDictionaryText", column = "atc_dictionary_text"),
            @Result(property = "medicationPt", column = "drug_pref_name"),
            @Result(property = "medicationGroupingName", column = "med_group_name"),
            @Result(property = "activeIngredient", column = "active_ingredient")
    })
    @Options(fetchSize = 5000)
    List<DrugDoseRaw> getRawData(@Param("datasetId") long datasetId);

    @Select("SELECT distinct mds_id                         AS event_id, "
            + "              aenat_num_act_taken "
            + "FROM result_trg_med_dos_schedule "
            + "INNER JOIN RESULT_AE_NUM_ACT_TAKEN ON MDS_ID = AENAT_MDS_ID "
            + "JOIN result_patient ON mds_pat_id = pat_id "
            + "JOIN result_study ON std_id = pat_std_id "
            + "INNER JOIN map_study_rule ON msr_study_code = std_name "
            + "WHERE msr_id      = #{datasetId}")
    @Results(value = {
            @Result(property = "drugDoseId", column = "event_id"),
            @Result(property = "aeNumCausedActionTaken", column = "aenat_num_act_taken")
    })
    @Options(fetchSize = 5000)
    List<DrugDoseRaw.AeNumCausedActionTaken> getAeNumCausedActionTaken(@Param("datasetId") long dataset);

    @Select("SELECT distinct mds_id                         AS event_id, "
            + "              mds_pat_id, "
            + "              aenat_num_act_taken "
            + "FROM result_trg_med_dos_schedule "
            + "JOIN result_patient ON mds_pat_id = pat_id "
            + "JOIN result_study ON std_id = pat_std_id "
            + "LEFT JOIN result_ae_num_act_taken aenat ON aenat_pat_id = pat_id AND aenat_mds_id = mds_id "
            + "INNER JOIN map_study_rule ON msr_study_code = std_name "
            + "WHERE msr_id      = #{datasetId}")
    @Results(value = {
            @Result(property = "drugDoseId", column = "event_id"),
            @Result(property = "subjectId", column = "mds_pat_id"),
            @Result(property = "aeNum", column = "aenat_num_act_taken")
    })
    @Options(fetchSize = 5000)
    List<DrugDoseRaw.AePtCausedActionTaken> getAePtCausedActionTaken(@Param("datasetId") long dataset);

    @Select("SELECT distinct mds_id                         AS event_id, "
            + "              aend_num_cycle_del  "
            + "FROM result_trg_med_dos_schedule "
            + "JOIN result_patient ON mds_pat_id = pat_id "
            + "JOIN result_study ON std_id = pat_std_id "
            + "INNER JOIN RESULT_AE_NUM_DEL ON MDS_ID = AEND_MDS_ID "
            + "INNER JOIN map_study_rule ON msr_study_code = std_name "
            + "WHERE msr_id      = #{datasetId}")
    @Results(value = {
            @Result(property = "drugDoseId", column = "event_id"),
            @Result(property = "aeNumCausedTreatmentCycleDelayed", column = "aend_num_cycle_del")
    })
    @Options(fetchSize = 5000)
    List<DrugDoseRaw.AeNumCausedTreatmentCycleDelayed> getAeNumCausedTreatmentCycleDelayed(@Param("datasetId") long dataset);

    @Select("SELECT distinct mds_id                         AS event_id, "
            + "              mds_pat_id, "
            + "              aend_num_cycle_del "
            + "FROM result_trg_med_dos_schedule "
            + "JOIN result_patient ON mds_pat_id = pat_id "
            + "JOIN result_study ON std_id = pat_std_id "
            + "LEFT JOIN result_ae_num_del aend ON aend_pat_id = pat_id AND aend_mds_id = mds_id "
            + "INNER JOIN map_study_rule ON msr_study_code = std_name "
            + "WHERE msr_id      = #{datasetId}")
    @Results(value = {
            @Result(property = "drugDoseId", column = "event_id"),
            @Result(property = "subjectId", column = "mds_pat_id"),
            @Result(property = "aeNumDel", column = "aend_num_cycle_del")
    })
    @Options(fetchSize = 5000)
    List<DrugDoseRaw.AePtCausedTreatmentCycleDelayed> getAePtCausedTreatmentCycleDelayed(
            @Param("datasetId") long dataset);
}
