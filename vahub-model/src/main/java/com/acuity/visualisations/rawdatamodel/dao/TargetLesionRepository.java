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
import com.acuity.visualisations.rawdatamodel.vo.TargetLesionRaw;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@AcuityRepository
public interface TargetLesionRepository extends RawDataRepository<TargetLesionRaw> {
    @Select("SELECT rtl_id, "
            + "  rtl_pat_id          AS subject_id, "
            + "  rtl_lesion_site     AS lesion_site, "
            + "  rtl_lesion_diameter AS lesion_diameter, "
            + "  rtl_lesion_number   AS lesion_number, "
            + "  rtl_lesion_date     AS lesion_date, "
            + "  rtl_visit_date      AS visit_date, "
            + "  rtl_visit_number    AS visit_number "
            + "FROM RESULT_RECIST_TARGET_LESION "
            + "JOIN result_patient "
            + "ON rtl_pat_id = pat_id "
            + "JOIN result_study "
            + "ON std_id = pat_std_id "
            + "JOIN map_study_rule "
            + "ON msr_study_code = std_name "
            + "WHERE msr_id      = #{datasetId}")
    @Results({
            @Result(property = "id", column = "rtl_id"),
            @Result(property = "subjectId", column = "subject_id"),
            @Result(property = "lesionSite", column = "lesion_site"),
            @Result(property = "lesionDiameter", column = "lesion_diameter"),
            @Result(property = "lesionNumber", column = "lesion_number"),
            @Result(property = "lesionDate", column = "lesion_date"),
            @Result(property = "visitDate", column = "visit_date"),
            @Result(property = "visitNumber", column = "visit_number")
    })
    @Options(fetchSize = 5000)
    List<TargetLesionRaw> getRawData(@Param("datasetId") long datasetId);
}
