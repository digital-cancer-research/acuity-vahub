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
import com.acuity.visualisations.rawdatamodel.vo.LiverRiskRaw;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@AcuityRepository
public interface LiverRiskRawDataRepository extends  RawDataRepository<LiverRiskRaw> {
    @Select("SELECT DISTINCT lrf_id, "
            + "              lrf_pat_id, "
            + "              lrf_value, "
            + "              lrf_occur, "
            + "              lrf_ref_period, "
            + "              lrf_details, "
            + "              lrf_start_date, "
            + "              lrf_stop_date, "
            + "              lrf_comment, "
            + "              lrf_pot_hys_law_case_num "
            + "FROM result_liver_risk_factors "
            + "INNER JOIN result_patient ON lrf_pat_id = pat_id "
            + "INNER JOIN result_study ON std_id = pat_std_id "
            + "INNER JOIN map_study_rule ON msr_study_code = std_name "
            + "WHERE msr_id      = #{datasetId}")
    @Results(value = {
            @Result(property = "id", column = "lrf_id"),
            @Result(property = "subjectId", column = "lrf_pat_id"),
            @Result(property = "value", column = "lrf_value"),
            @Result(property = "occurrence", column = "lrf_occur"),
            @Result(property = "referencePeriod", column = "lrf_ref_period"),
            @Result(property = "details", column = "lrf_details"),
            @Result(property = "startDate", column = "lrf_start_date"),
            @Result(property = "stopDate", column = "lrf_stop_date"),
            @Result(property = "comment", column = "lrf_comment"),
            @Result(property = "potentialHysLawCaseNum", column = "lrf_pot_hys_law_case_num")
    })
    @Options(fetchSize = 5000)
    List<LiverRiskRaw> getRawData(@Param("datasetId") long datasetId);
}
