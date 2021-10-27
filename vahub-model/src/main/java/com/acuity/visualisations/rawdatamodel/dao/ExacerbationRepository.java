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
import com.acuity.visualisations.rawdatamodel.vo.ExacerbationRaw;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@AcuityRepository
public interface ExacerbationRepository extends RawDataRepository<ExacerbationRaw> {
    @Select("SELECT distinct "
            + "exa_id, "
            + "exa_pat_id, "
            + "exa_severity, "
            + "exa_exac_start_date, "
            + "exa_exac_end_date, "
            + "exa_hospit, "
            + "exa_emer_trt, "
            + "exa_antibiotics_trt, "
            + "exa_depot_gcs, "
            + "exa_syscort_trt, "
            + "exa_ics_trt "
            + "FROM result_exacerbation resp "
            + "INNER JOIN result_patient pat ON pat_id=exa_pat_id "
            + "INNER JOIN result_study std ON std_id = pat_std_id "
            + "INNER JOIN map_study_rule msr ON (std_name = msr_study_code) "
            + "WHERE msr.msr_id = #{datasetId}")
    @Results(value = {
            @Result(property = "id", column = "EXA_ID"),
            @Result(property = "subjectId", column = "EXA_PAT_ID"),
            @Result(property = "exacerbationClassification", column = "EXA_SEVERITY"),
            @Result(property = "startDate", column = "EXA_EXAC_START_DATE"),
            @Result(property = "endDate", column = "EXA_EXAC_END_DATE"),
            @Result(property = "hospitalisation", column = "EXA_HOSPIT"),
            @Result(property = "emergencyRoomVisit", column = "EXA_EMER_TRT"),
            @Result(property = "antibioticsTreatment", column = "EXA_ANTIBIOTICS_TRT"),
            @Result(property = "depotCorticosteroidTreatment", column = "EXA_DEPOT_GCS"),
            @Result(property = "systemicCorticosteroidTreatment", column = "EXA_SYSCORT_TRT"),
            @Result(property = "increasedInhaledCorticosteroidTreatment", column = "EXA_ICS_TRT")
    })
    @Options(fetchSize = 5000)
    List<ExacerbationRaw> getRawData(@Param("datasetId") long datasetId);
}
