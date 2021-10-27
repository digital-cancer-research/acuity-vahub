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
import com.acuity.visualisations.rawdatamodel.vo.PkResultRaw;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@AcuityRepository
public interface PkResultRepository extends RawDataRepository<PkResultRaw> {
    @Select("SELECT DISTINCT stp_pat_id      AS subject_id,"
            + "  stp_id                      AS event_id,"
            + "  stp_parameter               AS parameter,"
            + "  stp_parameter_value         AS parameter_value,"
            + "  stp_parameter_value_unit    AS parameter_value_unit, "
            + "  stp_analyte                 AS analyte, "
            + "  TO_NUMBER(nullif(REGEXP_REPLACE(stp_actual_dose, '[^0-9.]', ''),'')) AS actual_dose, "
            + "  stp_visit                   AS visit, "
            + "  stp_visit_number AS visit_number, "
            + "  TO_NUMBER(nullif(REGEXP_REPLACE(stp_treatment, '[^0-9.]', ''),'')) AS treatment, "
            + "  stp_treatment_cycle AS treatment_cycle, "
            + "  stp_protocol_schd_start_day as protocol_schedule_start_day "
            + " FROM result_stacked_pk_results "
            + " JOIN result_patient ON pat_id = stp_pat_id"
            + " JOIN result_study ON std_id = pat_std_id"
            + " JOIN map_study_rule msr ON msr_study_code = std_name"
            + " WHERE msr.msr_id  = #{datasetId} AND "
            + " stp_parameter_value IS NOT NULL")
    @Results({
            @Result(property = "id", column = "event_id"),
            @Result(property = "subjectId", column = "subject_id"),
            @Result(property = "parameter", column = "parameter"),
            @Result(property = "parameterValue", column = "parameter_value"),
            @Result(property = "parameterUnit", column = "parameter_value_unit"),
            @Result(property = "analyte", column = "analyte"),
            @Result(property = "treatment", column = "treatment"),
            @Result(property = "treatmentCycle", column = "treatment_cycle"),
            @Result(property = "protocolScheduleStartDay", column = "protocol_schedule_start_day"),
            @Result(property = "actualDose", column = "actual_dose"),
            @Result(property = "visit", column = "visit"),
            @Result(property = "visitNumber", column = "visit_number")
    })
    @Options(fetchSize = 5000)
    List<PkResultRaw> getRawData(@Param("datasetId") long datasetId);
}
