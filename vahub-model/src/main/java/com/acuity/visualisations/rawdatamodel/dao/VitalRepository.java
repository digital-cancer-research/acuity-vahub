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
import com.acuity.visualisations.rawdatamodel.vo.VitalRaw;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@AcuityRepository
public interface VitalRepository extends RawDataRepository<VitalRaw> {
    @Select("  SELECT    "
            + "    vit.vit_id                                                                 AS id,"
            + "    pat_id                                                                     AS subject_id,"
            + "    vit.vit_test_name                                                          AS vitals_measurement,"
            + "    vit.vit_unit                                                               AS unit,"
            + "    vit.vit_value                                                              AS result_value,"
            + "    vit.vit_anatomical_location                                                AS anatomical_location,"
            + "    vit.vit_physical_position                                                  AS physical_position,"
            + "    tst_date                                                                   as measurement_date, "
            + "    tst_visit                                                                  AS visit_number,"
            + "    vit.vit_clinically_significant                                             AS clinically_significant,"
            + "    vit.vit_sch_timepoint                                                      AS scheduled_timepoint,"
            + "    vit.vit_last_ip_date                                                       AS last_drug_dose_date,"
            + "    vit.vit_last_ip_dose                                                       AS last_drug_dose,"
            + "    vit.vit_anatomical_side_interest                                           AS anatomical_side_interest,"
            + "    1                                                                          AS calc_daysincefirstdose_if_null, "
            + "    1                                                                          AS calc_chgefrombaseline_if_null "
            + "  FROM result_vitals vit"
            + "    INNER JOIN result_test ON tst_id = vit.vit_tst_id"
            + "    INNER JOIN result_patient ON pat_id = result_test.tst_pat_id"
            + "    INNER JOIN result_study ON std_id = pat_std_id"
            + "    INNER JOIN map_study_rule ON std_name = msr_study_code"
            + "  WHERE msr_id = #{datasetId} AND vit.vit_value IS NOT NULL AND pat_ip_dose_first_date IS NOT NULL")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "subjectId", column = "subject_id"),
            @Result(property = "vitalsMeasurement", column = "vitals_measurement"),
            @Result(property = "plannedTimePoint", column = "planned_time_point"),
            @Result(property = "measurementDate", column = "measurement_date"),
            @Result(property = "visitNumber", column = "visit_number"),
            @Result(property = "unit", column = "unit"),
            @Result(property = "resultValue", column = "result_value"),
            @Result(property = "analysisVisit", column = "analysis_visit"),
            @Result(property = "physicalPosition", column = "physical_position"),
            @Result(property = "clinicallySignificant", column = "clinically_significant"),
            @Result(property = "scheduleTimepoint", column = "scheduled_timepoint"),
            @Result(property = "sidesOfInterest", column = "anatomical_side_interest"),
            @Result(property = "lastDoseDate", column = "last_drug_dose_date"),
            @Result(property = "lastDoseAmount", column = "last_drug_dose"),
            @Result(property = "anatomicalLocation", column = "anatomical_location"),
            @Result(property = "calcDaysSinceFirstDoseIfNull", column = "calc_daysincefirstdose_if_null"),
            @Result(property = "calcChangeFromBaselineIfNull", column = "calc_chgefrombaseline_if_null")
    })
    @Options(fetchSize = 5000)
    @Override
    List<VitalRaw> getRawData(@Param("datasetId") long datasetId);
}
