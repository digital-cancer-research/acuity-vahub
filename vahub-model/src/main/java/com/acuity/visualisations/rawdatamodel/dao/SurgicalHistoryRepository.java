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
import com.acuity.visualisations.rawdatamodel.vo.SurgicalHistoryRaw;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@AcuityRepository
public interface SurgicalHistoryRepository extends RawDataRepository<SurgicalHistoryRaw> {
    @Select("SELECT DISTINCT sh_id AS event_id, "
            + "  sh_pat_id     AS subject_id, "
            + "  sh_procedure  AS procedure, "
            + "  sh_current    AS current_medication, "
            + "  sh_start_date AS start_date, "
            + "  sh_pt         AS pt, "
            + "  sh_hlt        AS hlt, "
            + "  sh_soc        AS soc "
            + "FROM result_surgical_history "
            + "INNER JOIN result_patient "
            + "ON sh_pat_id = pat_id "
            + "INNER JOIN result_study "
            + "ON std_id = pat_std_id "
            + "INNER JOIN map_study_rule "
            + "ON msr_study_code = std_name "
            + "WHERE msr_id      = #{datasetId}")
    @Results(value = {
            @Result(property = "id", column = "event_id"),
            @Result(property = "subjectId", column = "subject_id"),
            @Result(property = "surgicalProcedure", column = "procedure"),
            @Result(property = "preferredTerm", column = "pt"),
            @Result(property = "currentMedication", column = "current_medication"),
            @Result(property = "start", column = "start_date"),
            @Result(property = "pt", column = "pt"),
            @Result(property = "hlt", column = "hlt"),
            @Result(property = "soc", column = "soc")
    })
    @Options(fetchSize = 5000)
    List<SurgicalHistoryRaw> getRawData(@Param("datasetId") long datasetId);
}
