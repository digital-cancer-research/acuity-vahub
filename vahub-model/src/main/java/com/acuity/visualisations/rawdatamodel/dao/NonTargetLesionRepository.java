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
import com.acuity.visualisations.rawdatamodel.vo.NonTargetLesionRaw;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@AcuityRepository
public interface NonTargetLesionRepository extends RawDataRepository<NonTargetLesionRaw> {
    @Select("SELECT rntl_id        AS event_id, "
            + "  rntl_pat_id       AS subject_id, "
            + "  rntl_lesion_date  AS lesion_date, "
            + "  rntl_visit_number AS visit_number, "
            + "  rntl_visit_date   AS visit_date, "
            + "  rntl_lesion_site  AS lesion_site, "
            + "  COALESCE(rrl_name, rntl_response)          AS response, "
            + "  COALESCE(rrl_shortname, rntl_response)     AS response_short "
            + "FROM result_recist_nontarget_lesion "
            + "INNER JOIN result_patient "
            + "ON rntl_pat_id = pat_id "
            + "INNER JOIN result_study "
            + "ON PAT_STD_ID = std_id "
            + "INNER JOIN map_study_rule "
            + "ON msr_study_code = std_name "
            + "LEFT JOIN util_recist_response_lookup "
            + "ON (((LOWER(rntl_response) LIKE rrl_regex) "
            + "OR rntl_response  = CAST(rrl_code AS VARCHAR(5)) "
            + "OR upper(rrl_shortname) = upper(rntl_response))) "
            + "WHERE msr_id     = #{datasetId}")
    @Results({
            @Result(property = "id", column = "event_id"),
            @Result(property = "subjectId", column = "subject_id"),
            @Result(property = "lesionDate", column = "lesion_date"),
            @Result(property = "visitNumber", column = "visit_number"),
            @Result(property = "visitDate", column = "visit_date"),
            @Result(property = "lesionSite", column = "lesion_site"),
            @Result(property = "response", column = "response"),
            @Result(property = "responseShort", column = "response_short"),
    })
    @Options(fetchSize = 5000)
    List<NonTargetLesionRaw> getRawData(@Param("datasetId") long datasetId);
}

