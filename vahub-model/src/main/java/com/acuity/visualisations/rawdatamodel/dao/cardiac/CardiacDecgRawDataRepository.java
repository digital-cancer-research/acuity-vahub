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
import com.acuity.visualisations.rawdatamodel.dao.api.RawDataRepository;
import com.acuity.visualisations.rawdatamodel.vo.CardiacDecgRaw;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@AcuityRepository
public interface CardiacDecgRawDataRepository extends RawDataRepository<CardiacDecgRaw> {

    @Select("SELECT "
            + "decg_id, "
            + "pat_id, "
            + "tst_date, "
            + "tst_visit, "
            + "decg_sch_timepoint, "
            + "decg_measurment_label, "
            + "decg_measurment_value, "
            + "COALESCE(sel_lookup, decg_evaluation)  AS evaluation, "
            + "COALESCE(scl_lookup, decg_significant) AS significant, "
            + "decg_method, "
            + "decg_beat_group_num, "
            + "decg_beat_num_in_beat_group, "
            + "decg_num_beats_avr_beat, "
            + "decg_beat_group_length_sec, "
            + "decg_comment, "
            + "decg_measurment_value, "
            + "1 AS calc_chgefrombaseline_if_null , "
            + "1 AS calc_daysincefirstdose_if_null "
            + "FROM result_decg "
            + "JOIN result_test ON tst_id = decg_tst_id "
            + "JOIN result_patient ON pat_id = tst_pat_id "
            + "JOIN result_study ON std_id = pat_std_id "
            + "JOIN map_study_rule ON std_name = msr_study_code "
            + "LEFT JOIN util_ecg_evaluation_lookup ON (UPPER(decg_evaluation) = sel_label) "
            + "LEFT JOIN util_ecg_significant_lookup ON (UPPER(decg_significant) = upper(scl_label)) "
            + "WHERE msr_id = #{datasetId} "
            + "AND pat_ip_dose_first_date IS NOT NULL "
    )
    @Results(value = {
            @Result(property = "id", column = "decg_id"),
            @Result(property = "subjectId", column = "pat_id"),
            @Result(property = "measurementName", column = "decg_measurment_label"),
            @Result(property = "measurementTimePoint", column = "tst_date"),
            @Result(property = "visitNumber", column = "tst_visit"),
            @Result(property = "resultValue", column = "decg_measurment_value"),
            @Result(property = "protocolScheduleTimepoint", column = "decg_sch_timepoint"),
            @Result(property = "ecgEvaluation", column = "evaluation"),
            @Result(property = "clinicallySignificant", column = "significant"),
            @Result(property = "method", column = "decg_method"),
            @Result(property = "beatGroupNumber", column = "decg_beat_group_num"),
            @Result(property = "beatNumberWithinBeatGroup", column = "decg_beat_num_in_beat_group"),
            @Result(property = "numberOfBeatsInAverageBeat", column = "decg_num_beats_avr_beat"),
            @Result(property = "beatGroupLengthInSec", column = "decg_beat_group_length_sec"),
            @Result(property = "comment", column = "decg_comment"),
            @Result(property = "calcChangeFromBaselineIfNull", column = "calc_chgefrombaseline_if_null"),
            @Result(property = "calcDaysSinceFirstDoseIfNull", column = "calc_daysincefirstdose_if_null")
    })
    @Options(fetchSize = 5000)
    List<CardiacDecgRaw> getRawData(@Param("datasetId") Long datasetId);
}
