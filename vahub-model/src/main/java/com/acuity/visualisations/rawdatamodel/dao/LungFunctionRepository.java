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
import com.acuity.visualisations.rawdatamodel.vo.LungFunctionRaw;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@AcuityRepository
public interface LungFunctionRepository extends RawDataRepository<LungFunctionRaw> {
    @Select("SELECT DISTINCT  "
            + "  lng_id,  "
            + "  pat_id,  "
            + "  lng_measurement                          AS measurement_name,  "
            + "  lng_visit_date                           AS measurement_time_point,  "
            + "  lng_prot_schedule                        AS protocol_schedule_time_point,  "
            + "  lng_visit                                AS visit,  "
            + "  lng_result                               AS value,  "
            + "  lng_visit_date                           AS visit_date,  "
            + "  1                                        AS calc_daysincefirstdose_if_null, "
            + "  1                                        AS calc_chgefrombaseline_if_null "
            + "FROM result_lungfunc  "
            + "JOIN result_patient  "
            + "ON lng_pat_id = pat_id  "
            + "JOIN result_study  "
            + "ON std_id = pat_std_id  "
            + "INNER JOIN map_study_rule  "
            + "ON msr_study_code = std_name  "
            + "WHERE lng_result IS NOT NULL  "
            + "AND pat_ip_dose_first_date IS NOT NULL  "
            + "AND msr_id = #{datasetId}")
    @Results(value = {
            @Result(property = "id", column = "lng_id"),
            @Result(property = "subjectId", column = "pat_id"),
            @Result(property = "measurementNameRaw", column = "measurement_name"),
            @Result(property = "measurementTimePoint", column = "measurement_time_point"),
            @Result(property = "protocolScheduleTimepoint", column = "protocol_schedule_time_point"),
            @Result(property = "visit", column = "visit"),
            @Result(property = "value", column = "value"),
            @Result(property = "visitDate", column = "visit_date"),
            @Result(property = "calcDaysSinceFirstDoseIfNull", column = "calc_daysincefirstdose_if_null"),
            @Result(property = "calcChangeFromBaselineIfNull", column = "calc_chgefrombaseline_if_null")
    })
    @Options(fetchSize = 5000)
    @Override
    List<LungFunctionRaw> getRawData(long datasetId);
}
