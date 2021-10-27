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
import com.acuity.visualisations.rawdatamodel.vo.PathologyRaw;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@AcuityRepository
public interface PathologyRepository extends RawDataRepository<PathologyRaw> {
    @Select("SELECT pth_id              AS event_id, "
            + "pth_pat_id               AS subject_id, "
            + "pth_date                 AS diagnosis_date, "
            + "pth_his_type             AS his_type, "
            + "pth_his_type_details     AS his_type_details, "
            + "pth_tumour_grade         AS tumour_grade, "
            + "pth_stage                AS stage, "
            + "pth_tumor_location       AS location, "
            + "pth_prim_tum_status      AS tumour_status, "
            + "pth_nodes_status         AS nodes_status, "
            + "pth_metastases_status    AS metastases_status, "
            + "pth_determ_method        AS method, "
            + "pth_other_methods        AS other_methods "
            + "FROM result_pathology "
            + "JOIN result_patient "
            + "ON pth_pat_id = pat_id "
            + "JOIN result_study "
            + "ON std_id = pat_std_id "
            + "INNER JOIN map_study_rule "
            + "ON msr_study_code = std_name "
            + "WHERE msr_id      = #{datasetId}")
    @Results(value = {
            @Result(property = "id", column = "event_id"),
            @Result(property = "subjectId", column = "subject_id"),
            @Result(property = "determMethod", column = "method"),
            @Result(property = "hisType", column = "his_type"),
            @Result(property = "date", column = "diagnosis_date"),
            @Result(property = "hisTypeDetails", column = "his_type_details"),
            @Result(property = "tumourGrade", column = "tumour_grade"),
            @Result(property = "primTumour", column = "tumour_status"),
            @Result(property = "nodesStatus", column = "nodes_status"),
            @Result(property = "metastasesStatus", column = "metastases_status"),
            @Result(property = "stage", column = "stage"),
            @Result(property = "tumourLocation", column = "location")
    })
    @Options(fetchSize = 5000)
    List<PathologyRaw> getRawData(@Param("datasetId") long datasetId);
}
