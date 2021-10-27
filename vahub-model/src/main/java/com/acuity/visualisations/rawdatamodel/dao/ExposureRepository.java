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
import com.acuity.visualisations.rawdatamodel.vo.ExposureRaw;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@AcuityRepository
public interface ExposureRepository extends RawDataRepository<ExposureRaw> {
    @Select("SELECT DISTINCT"
            + "    e.*, "
            + "    pat_id, "
            + "    spc_visit, "
            + "    spc_visit_date, "
            + "    spc_protocol_schedule_day, "
            + "    spc_protocol_schedule_hour, "
            + "    spc_protocol_schedule_minute, "
            + "    spc_specimen_date, "
            + "    spc_drug_adm_date, "
            + "    spc_protocol_schedule_day "
            + "FROM "
            + "    result_pk_concentration   e "
            + "    INNER JOIN result_patient            pat ON pat_id = pkc_pat_id "
            + "    INNER JOIN result_specimen_collection ON pat_id = spc_pat_id "
            + "                                             AND pkc_specimen_id = spc_specimen_id "
            + "    INNER JOIN result_study              std ON std_id = pat_std_id "
            + "    INNER JOIN map_study_rule            msr ON std_name = msr_study_code "
            + "WHERE "
            + "    msr.msr_id = #{datasetId} "
            + "    AND pkc_analyte IS NOT NULL "
            + "    AND pkc_analyte_concentration IS NOT NULL "
            + "    AND ( spc_protocol_schedule_hour IS NOT NULL "
            + "          OR spc_protocol_schedule_minute IS NOT NULL )")
    @Results({
            @Result(property = "subjectId", column = "pat_id"),
            @Result(property = "id", column = "pkc_id"),
            @Result(property = "analyte", column = "pkc_analyte"),
            @Result(property = "analyteConcentration", column = "pkc_analyte_concentration"),
            @Result(property = "analyteUnit", column = "pkc_analyte_concentration_unit"),
            @Result(property = "treatment", column = "pkc_treatment"),
            @Result(property = "treatmentCycle", column = "pkc_treatment_cycle"),
            @Result(property = "visitNumber", column = "spc_visit"),
            @Result(property = "drugAdministrationDate", column = "spc_drug_adm_date"),
            @Result(property = "protocolScheduleDay", column = "spc_protocol_schedule_day"),
            @Result(property = "visitDate", column = "spc_visit_date"),
            @Result(property = "nominalDay", column = "spc_protocol_schedule_day"),
            @Result(property = "nominalHour", column = "spc_protocol_schedule_hour"),
            @Result(property = "nominalMinute", column = "spc_protocol_schedule_minute"),
            @Result(property = "lowerLimit", column = "pkc_lower_limit"),
            @Result(property = "actualSamplingDate", column = "spc_specimen_date")
    })
    @Options(fetchSize = 5000)
    List<ExposureRaw> getRawData(@Param("datasetId") long datasetId);
}
