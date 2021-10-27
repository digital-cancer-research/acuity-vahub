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

package com.acuity.visualisations.rawdatamodel.dao.cardiac;

import com.acuity.visualisations.common.lookup.AcuityRepository;
import com.acuity.visualisations.rawdatamodel.vo.CardiacRaw;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@AcuityRepository
public interface CardiacLvefRawDataRepository extends CardiacRepository {

    @Select("SELECT "
            + "lvf_id, "
            + "pat_id, "
            + "tst_date, "
            + "tst_visit, "
            + "'Ejection fraction' measurement_category, "
            + "'LVEF' AS measurement_name, "
            + "lvf_lvef, "
            + "1 AS calc_chgefrombaseline_if_null , "
            + "1 AS calc_daysincefirstdose_if_null "
            + "FROM result_lvef "
            + "JOIN result_test ON tst_id = lvf_tst_id "
            + "JOIN result_patient ON pat_id = tst_pat_id "
            + "JOIN result_study ON std_id = pat_std_id "
            + "JOIN map_study_rule ON std_name = msr_study_code "
            + "WHERE msr_id = #{datasetId} "
            + "AND pat_ip_dose_first_date IS NOT NULL "
            + "AND lvf_lvef IS NOT NULL "
    )
    @Results(value = {
            @Result(property = "id", column = "lvf_id"),
            @Result(property = "subjectId", column = "pat_id"),
            @Result(property = "measurementCategory", column = "measurement_category"),
            @Result(property = "measurementName", column = "measurement_name"),
            @Result(property = "measurementTimePoint", column = "tst_date"),
            @Result(property = "calcChangeFromBaselineIfNull", column = "calc_chgefrombaseline_if_null"),
            @Result(property = "calcDaysSinceFirstDoseIfNull", column = "calc_daysincefirstdose_if_null"),
            @Result(property = "visitNumber", column = "tst_visit"),
            @Result(property = "resultValue", column = "lvf_lvef")
    })
    @Options(fetchSize = 5000)
    List<CardiacRaw> getRawData(@Param("datasetId") long datasetId);
}
