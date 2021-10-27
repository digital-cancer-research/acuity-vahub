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
import com.acuity.visualisations.rawdatamodel.vo.LabRaw;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@AcuityRepository
public interface LabRepository extends RawDataRepository<LabRaw> {
    @Select({" SELECT DISTINCT lab_id, "
            + " pat_id, "
            + " tst_date, "
            + " COALESCE(lcd_definition, cll_test_name, lcl_test_name, lab_code, 'EMPTY') AS lab_code, "
            + " COALESCE(lkp.labcgl_category, 'EMPTY') AS lab_category, "
            + " 1 AS calc_chgefrombaseline_if_null,  "
            + " 1 AS calc_daysincefirstdose_if_null, "
            + "  lab_value, "
            + "  lab_unit, "
            + "  lab_ref_high, "
            + "  lab_ref_low, "
            + "  lab_comment, "
            + "  lab_src_id, "
            + "  lab_src_type, "
            + "  lab_value_dipstick, "
            + "  lab_sch_timepoint, "
            + "  tst_visit "
            + "FROM result_laboratory "
            + "INNER JOIN result_test ON tst_id=lab_tst_id "
            + "INNER JOIN result_patient ON pat_id=tst_pat_id "
            + "INNER JOIN result_study ON std_id = pat_std_id "
            + "INNER JOIN map_study_rule ON (std_name = msr_study_code) "
            + "LEFT JOIN map_custom_labcode_lookup ON (upper(lab_code) = upper(cll_labcode) AND cll_msr_id = msr_id AND msr_use_alt_lab_codes = 1) "
            + "LEFT JOIN util_labcode_lookup ON (upper(lab_code) = upper(lcl_labcode)) "
            + "LEFT JOIN util_labcode_synonym ON (upper(COALESCE(cll_test_name, lcl_test_name, lab_code)) = upper(lcs_synonym)) "
            + "LEFT JOIN util_labcode_dictionary ON (lcs_lcd_id = lcd_id) "
            + "LEFT JOIN util_labctcg_lookup lkp ON lab_code = lkp.labcgl_code "
            + "or (upper(labcgl_test_name) = upper(COALESCE(lcd_definition, cll_test_name, lcl_test_name, lab_code, 'EMPTY')) "
            + "and upper(lab_code) = upper(labcgl_test_name)) "
            + "LEFT JOIN map_study_lab_group ON (mslg_msr_id = msr_id) "
            + "LEFT JOIN map_lab_group_rule ON (mslg_lab_group_id = mlgr_id) "
            + "LEFT JOIN map_lab_group_value_rule ON (MLGR_ID = mlgv_group_id AND trim(upper(lab_code))   = trim(upper(mlgv_lab_code))) "
            + "WHERE (lab_value IS NOT NULL OR lab_value_dipstick <> '') "
            + "AND pat_ip_dose_first_date IS NOT NULL "
            + "AND msr_id = #{datasetId}"})
    @Results(value = {
            @Result(property = "id", column = "lab_id"),
            @Result(property = "subjectId", column = "pat_id"),
            @Result(property = "measurementTimePoint", column = "tst_date"),
            @Result(property = "visitDate", column = "tst_date"),
            @Result(property = "visitNumber", column = "tst_visit"),
            @Result(property = "labCode", column = "lab_code"),
            @Result(property = "category", column = "lab_category"),
            @Result(property = "calcDaysSinceFirstDoseIfNull", column = "calc_daysincefirstdose_if_null"),
            @Result(property = "value", column = "lab_value"),
            @Result(property = "unit", column = "lab_unit"),
            @Result(property = "refHigh", column = "lab_ref_high"),
            @Result(property = "refLow", column = "lab_ref_low"),
            @Result(property = "calcChangeFromBaselineIfNull", column = "calc_chgefrombaseline_if_null"),
            @Result(property = "protocolScheduleTimepoint", column = "lab_sch_timepoint"),
            @Result(property = "valueDipstick", column = "lab_value_dipstick"),
            @Result(property = "comment", column = "lab_comment"),
            @Result(property = "sourceId", column = "lab_src_id"),
            @Result(property = "sourceType", column = "lab_src_type")
    })
    @Options(fetchSize = 5000)
    List<LabRaw> getRawData(@Param("datasetId") long datasetId);
}
