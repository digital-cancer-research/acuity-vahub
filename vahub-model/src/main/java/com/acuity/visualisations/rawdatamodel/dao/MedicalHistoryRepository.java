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
import com.acuity.visualisations.rawdatamodel.vo.MedicalHistoryRaw;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@AcuityRepository
public interface MedicalHistoryRepository extends RawDataRepository<MedicalHistoryRaw> {
    @Select("SELECT mh_id              AS event_id, "
            + "  mh_pat_id             AS subject_id, "
            + "  mh_category           AS category, "
            + "  mh_term               AS term, "
            + "  mh_pt_name            AS preferred_term, "
            + "  mh_condition_status   AS condition_status, "
            + "  mh_current_medication AS current_medication, "
            + "  mh_start_date         AS start_date, "
            + "  mh_end_date           AS end_date, "
            + "  mh_hlt_name           AS hlt, "
            + "  mh_soc_name           AS soc "
            + "FROM result_medical_history "
            + "JOIN result_patient "
            + "ON mh_pat_id = pat_id "
            + "JOIN result_study "
            + "ON std_id = pat_std_id "
            + "INNER JOIN map_study_rule "
            + "ON msr_study_code = std_name "
            + "WHERE msr_id      = #{datasetId}")
    @Results(value = {
            @Result(property = "id", column = "event_id"),
            @Result(property = "subjectId", column = "subject_id"),
            @Result(property = "category", column = "category"),
            @Result(property = "term", column = "term"),
            @Result(property = "preferredTerm", column = "preferred_term"),
            @Result(property = "conditionStatus", column = "condition_status"),
            @Result(property = "currentMedication", column = "current_medication"),
            @Result(property = "start", column = "start_date"),
            @Result(property = "end", column = "end_date"),
            @Result(property = "hlt", column = "hlt"),
            @Result(property = "soc", column = "soc")
    })
    @Options(fetchSize = 5000)
    List<MedicalHistoryRaw> getRawData(@Param("datasetId") long datasetId);
}
