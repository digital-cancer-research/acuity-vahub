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
import com.acuity.visualisations.rawdatamodel.vo.LiverDiagRaw;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@AcuityRepository
public interface LiverDiagRepository extends RawDataRepository<LiverDiagRaw> {
    @Select("SELECT DISTINCT  "
            + "  li_id,  "
            + "  li_pat_id                                                                  AS pat_id, "
            + "  li_value                                                                   AS liver_diag_value, "
            + "  li_spec                                                                    AS liver_diag_spec, "
            + "  li_date                                                                    AS liver_diag_date, "
            + "  li_res                                                                     AS liver_diag_result, "
            + "  li_pot_hys_law_case_num                                                    AS potential_hys_law_case_num "
            + "FROM result_liver  "
            + "JOIN result_patient  "
            + "ON li_pat_id = pat_id  "
            + "JOIN result_study  "
            + "ON std_id = pat_std_id  "
            + "INNER JOIN map_study_rule  "
            + "ON msr_study_code = std_name  "
            + "WHERE msr_id = #{datasetId}")
    @Results(value = {
            @Result(property = "id", column = "li_id"),
            @Result(property = "subjectId", column = "pat_id"),
            @Result(property = "liverDiagInv", column = "liver_diag_value"),
            @Result(property = "liverDiagInvSpec", column = "liver_diag_spec"),
            @Result(property = "liverDiagInvDate", column = "liver_diag_date"),
            @Result(property = "liverDiagInvResult", column = "liver_diag_result"),
            @Result(property = "potentialHysLawCaseNum", column = "potential_hys_law_case_num")
    })
    @Options(fetchSize = 5000)
    @Override
    List<LiverDiagRaw> getRawData(@Param("datasetId") long datasetId);
}
