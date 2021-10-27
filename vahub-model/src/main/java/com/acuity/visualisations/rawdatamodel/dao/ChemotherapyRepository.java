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
import com.acuity.visualisations.rawdatamodel.vo.ChemotherapyRaw;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@AcuityRepository
public interface ChemotherapyRepository extends RawDataRepository<ChemotherapyRaw> {
    @Select("SELECT DISTINCT pat_id AS subject_id,"
            + "  chemo_id                       AS event_id,"
            + "  chemo_start_date               AS start_date,"
            + "  chemo_end_date                 AS end_date,"
            + "  chemo_preferred_name_of_med    AS preferred_name_of_med, "
            + "  chemo_therapy_reason           AS therapy_reason, "
            + "  chemo_cancer_therapy_agent     AS therapy_agent, "
            + "  chemo_class                    AS therapy_class, "
            + "  chemo_treatment_status         AS treatment_status, "
            + "  chemo_time_status              AS time_status, "
            + "  chemo_num_of_cycles            AS num_of_cycles, "
            + "  chemo_route                    AS route, "
            + "  chemo_best_response            AS best_response, "
            + "  chemo_reason_for_failure       AS reason_for_failure"
            + " FROM result_chemotherapy"
            + " JOIN result_patient ON pat_id = chemo_pat_id"
            + " JOIN result_study ON std_id = pat_std_id"
            + " JOIN map_study_rule msr ON msr_study_code = std_name"
            + " WHERE msr.msr_id  = #{datasetId}")
    @Results({
            @Result(property = "id", column = "event_id"),
            @Result(property = "subjectId", column = "subject_id"),
            @Result(property = "startDate", column = "start_date"),
            @Result(property = "endDate", column = "end_date"),
            @Result(property = "preferredMed", column = "preferred_name_of_med"),
            @Result(property = "therapyReason", column = "therapy_reason"),
            @Result(property = "agent", column = "therapy_agent"),
            @Result(property = "therapyClass", column = "therapy_class"),
            @Result(property = "treatmentStatus", column = "treatment_status"),
            @Result(property = "timeStatus", column = "time_status"),
            @Result(property = "numOfCycles", column = "num_of_cycles"),
            @Result(property = "route", column = "route"),
            @Result(property = "bestResponse", column = "best_response"),
            @Result(property = "failureReason", column = "reason_for_failure")
    })
    @Options(fetchSize = 5000)
    List<ChemotherapyRaw> getRawData(@Param("datasetId") long datasetId);
}
