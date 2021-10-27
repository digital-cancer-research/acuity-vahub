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

package com.acuity.visualisations.rawdatamodel.dao.cardiac;

import com.acuity.visualisations.common.lookup.AcuityRepository;
import com.acuity.visualisations.rawdatamodel.vo.CardiacRaw;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@AcuityRepository
public interface CardiacEcgRawDataRepository extends CardiacRepository {

    @Select("SELECT "
            + "eg_id, "
            + "pat_id, "
            + "tst_date, "
            + "tst_visit, "
            + "eg_sch_timepoint, "
            + "eg_test_name, "
            + "eg_result_unit, "
            + "eg_test_result, "
            + "COALESCE(sel_lookup, eg_evaluation)  AS evaluation, "
            + "COALESCE(scl_lookup, eg_significant) AS significant, "
            + "'ECG' AS measurement_category, "
            + "eg_date_last_dose, "
            + "eg_last_dose_amount, "
            + "eg_method, "
            + "eg_atrial_fibr, "
            + "eg_sinus_rhythm, "
            + "eg_reas_no_sinus_rhythm, "
            + "eg_heart_rhythm, "
            + "eg_heart_rhythm_oth, "
            + "eg_extra_systoles, "
            + "eg_specify_extra_systoles, "
            + "eg_type_cond, "
            + "eg_cond, "
            + "eg_reas_abnormal_cond, "
            + "eg_stt_changes, "
            + "eg_st_segment, "
            + "eg_wave, "
            + "1 AS calc_chgefrombaseline_if_null , "
            + "1 AS calc_daysincefirstdose_if_null "
            + "FROM result_eg "
            + "JOIN result_test ON tst_id = eg_tst_id "
            + "JOIN result_patient ON pat_id = tst_pat_id "
            + "JOIN result_study ON std_id = pat_std_id "
            + "JOIN map_study_rule ON std_name = msr_study_code "
            + "LEFT JOIN util_ecg_evaluation_lookup ON (UPPER(eg_evaluation) = sel_label) "
            + "LEFT JOIN util_ecg_significant_lookup ON (UPPER(eg_significant) = upper(scl_label)) "
            + "WHERE msr_id = #{datasetId} "
            + "AND pat_ip_dose_first_date IS NOT NULL "
            + "AND eg_test_result IS NOT NULL "
    )
    @Results(value = {
            @Result(property = "id", column = "eg_id"),
            @Result(property = "subjectId", column = "pat_id"),
            @Result(property = "measurementCategory", column = "measurement_category"),
            @Result(property = "measurementName", column = "eg_test_name"),
            @Result(property = "measurementTimePoint", column = "tst_date"),
            @Result(property = "visitNumber", column = "tst_visit"),
            @Result(property = "resultValue", column = "eg_test_result"),
            @Result(property = "resultUnit", column = "eg_result_unit"),
            @Result(property = "protocolScheduleTimepoint", column = "eg_sch_timepoint"),
            @Result(property = "ecgEvaluation", column = "evaluation"),
            @Result(property = "clinicallySignificant", column = "significant"),
            @Result(property = "dateOfLastDose", column = "eg_date_last_dose"),
            @Result(property = "lastDoseAmount", column = "eg_last_dose_amount"),
            @Result(property = "method", column = "eg_method"),
            @Result(property = "atrialFibrillation", column = "eg_atrial_fibr"),
            @Result(property = "sinusRhythm", column = "eg_sinus_rhythm"),
            @Result(property = "reasonNoSinusRhythm", column = "eg_reas_no_sinus_rhythm"),
            @Result(property = "heartRhythm", column = "eg_heart_rhythm"),
            @Result(property = "heartRhythmOther", column = "eg_heart_rhythm_oth"),
            @Result(property = "extraSystoles", column = "eg_extra_systoles"),
            @Result(property = "specifyExtraSystoles", column = "eg_specify_extra_systoles"),
            @Result(property = "typeOfConduction", column = "eg_type_cond"),
            @Result(property = "conduction", column = "eg_cond"),
            @Result(property = "reasonAbnormalConduction", column = "eg_reas_abnormal_cond"),
            @Result(property = "sttChanges", column = "eg_stt_changes"),
            @Result(property = "stSegment", column = "eg_st_segment"),
            @Result(property = "wave", column = "eg_wave"),
            @Result(property = "calcChangeFromBaselineIfNull", column = "calc_chgefrombaseline_if_null"),
            @Result(property = "calcDaysSinceFirstDoseIfNull", column = "calc_daysincefirstdose_if_null"),
    })
    @Options(fetchSize = 5000)
    List<CardiacRaw> getRawData(@Param("datasetId") long datasetId);

}
