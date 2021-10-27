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
import com.acuity.visualisations.rawdatamodel.vo.PatientDataRaw;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@AcuityRepository
public interface PatientDataRepository extends RawDataRepository<PatientDataRaw> {
    @Select("SELECT DISTINCT RD_PAT_ID      AS subject_id,"
            + "  RD_ID                      AS event_id,"
            + "  RD_MEASUREMENT_NAME        AS measurement_name,"
            + "  RD_VALUE                   AS value,"
            + "  RD_UNIT                    AS unit, "
            + "  RD_MEASUREMENT_DATE        AS measurement_date, "
            + "  RD_REPORT_DATE             AS report_date, "
            + "  RD_COMMENT                 AS rd_comment, "
            + "  RD_SRC_ID                  AS src_id, "
            + "  RD_SRC_TYPE                AS src_type "
            + " FROM RESULT_PATIENT_REPORTED_DATA "
            + " JOIN result_patient ON pat_id = rd_pat_id"
            + " JOIN result_study ON std_id = pat_std_id"
            + " JOIN map_study_rule msr ON msr_study_code = std_name"
            + " WHERE msr.msr_id  = #{datasetId}")
    @Results({
            @Result(property = "id", column = "event_id"),
            @Result(property = "subjectId", column = "subject_id"),
            @Result(property = "measurementName", column = "start_date"),
            @Result(property = "value", column = "value"),
            @Result(property = "unit", column = "unit"),
            @Result(property = "measurementDate", column = "measurement_date"),
            @Result(property = "reportDate", column = "report_date"),
            @Result(property = "comment", column = "comment"),
            @Result(property = "sourceType", column = "src_type"),
            @Result(property = "sourceId", column = "src_id")
    })
    @Options(fetchSize = 5000)
    List<PatientDataRaw> getRawData(@Param("datasetId") long datasetId);
}
