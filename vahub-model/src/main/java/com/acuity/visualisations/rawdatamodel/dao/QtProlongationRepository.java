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
import com.acuity.visualisations.rawdatamodel.vo.QtProlongationRaw;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@AcuityRepository
public interface QtProlongationRepository extends RawDataRepository<QtProlongationRaw> {
    @Select("WITH ecg AS (SELECT pat_ip_dose_first_date, "
            + "                   eg_id          ecg_id, "
            + "                   tst_date, "
            + "                   tst_visit, "
            + "                   tst_pat_id, "
            + "                   eg_test_name   ecg_test, "
            + "                   eg_test_result ecg_value, "
            + "                   'ECG'          measurement_category "
            + "            FROM result_eg "
            + "                     LEFT JOIN result_test ON tst_id = eg_tst_id "
            + "                     INNER JOIN result_patient ON pat_id = tst_pat_id "
            + "                     INNER JOIN result_study ON std_id = pat_std_id "
            + "                     INNER JOIN map_study_rule ON std_name = msr_study_code "
            + "            WHERE msr_id = #{datasetId} "
            + "            UNION "
            + "            SELECT pat_ip_dose_first_date, "
            + "                   decg.decg_id                          ecg_id, "
            + "                   tst_date, "
            + "                   tst_visit, "
            + "                   tst_pat_id, "
            + "                   decg.decg_measurment_label            ecg_test, "
            + "                   TO_NUMBER(decg.decg_measurment_value) ecg_value, "
            + "                   'ECG'                                 measurement_category "
            + "            FROM result_decg decg "
            + "                     INNER JOIN result_test ON tst_id = decg_tst_id "
            + "                     LEFT JOIN result_decg ev ON (tst_id = ev.decg_tst_id AND ev.decg_measurment_label = 'INTP') "
            + "                     INNER JOIN result_patient ON pat_id = tst_pat_id "
            + "                     INNER JOIN result_study ON std_id = pat_std_id "
            + "                     INNER JOIN map_study_rule ON std_name = msr_study_code "
            + "            WHERE ACUITY_UTILS.IS_NUMBER(decg.decg_measurment_value) = 1 "
            + "                  AND decg.decg_measurment_label != 'INTP' "
            + "                  AND msr_id = #{datasetId} "
            + "            UNION "
            + "            SELECT pat_ip_dose_first_date, "
            + "                   lvf_id              ecg_id, "
            + "                   tst_date, "
            + "                   tst_visit, "
            + "                   tst_pat_id, "
            + "                   'LVEF'              ecg_test, "
            + "                   lvf_lvef            ecg_value, "
            + "                   'Ejection fraction' measurement_category "
            + "            FROM result_lvef "
            + "                     LEFT JOIN result_test ON tst_id = lvf_tst_id "
            + "                     INNER JOIN result_patient ON pat_id = tst_pat_id "
            + "                     INNER JOIN result_study ON std_id = pat_std_id "
            + "                     INNER JOIN map_study_rule ON std_name = msr_study_code "
            + "                 WHERE msr_id = #{datasetId} "
            + "    ) "
            + "SELECT DISTINCT ecg_id                 event_id, "
            + "                tst_date               measurement_time_point, "
            + "                tst_visit              visit_number, "
            + "                tst_pat_id             subject_id, "
            + "                ecg_test               measurement_name, "
            + "                ecg_value              result_value, "
            + "                pat_ip_dose_first_date dose_first_date, "
            + "                measurement_category   measurement_category, "
            + "                ao_result              alert_level, "
            + "                src_name, "
            + "                src_version, "
            + "                src_type "
            + "FROM ecg "
            + "         JOIN result_algorithm_outcomes ON ecg_id = ao_event_id "
            + "         JOIN result_source ON ao_src_id = src_id "
            + "WHERE ao_event_type = 'ECG'")
    @Results({
            @Result(property = "id", column = "event_id"),
            @Result(property = "subjectId", column = "subject_id"),
            @Result(property = "measurementCategory", column = "measurement_category"),
            @Result(property = "measurementName", column = "measurement_name"),
            @Result(property = "measurementTimePoint", column = "measurement_time_point"),
            @Result(property = "doseFirstDate", column = "dose_first_date"),
            @Result(property = "visitNumber", column = "visit_number"),
            @Result(property = "resultValue", column = "result_value"),
            @Result(property = "alertLevel", column = "alert_level"),
            @Result(property = "sourceName", column = "src_name"),
            @Result(property = "sourceVersion", column = "src_version"),
            @Result(property = "sourceType", column = "src_type")
    })
    @Options(fetchSize = 5000)
    List<QtProlongationRaw> getRawData(@Param("datasetId") long datasetId);
}
