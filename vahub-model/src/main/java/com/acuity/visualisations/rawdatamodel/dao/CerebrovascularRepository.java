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
import com.acuity.visualisations.rawdatamodel.vo.CerebrovascularRaw;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@AcuityRepository
public interface CerebrovascularRepository extends RawDataRepository<CerebrovascularRaw> {
    @Select("SELECT cerebro.*, std.std_id, pat.pat_part "
            + "from RESULT_CEREBROVASCULAR cerebro "
            + "INNER JOIN result_patient pat "
            + "ON pat_id=cer_pat_id "
            + "INNER JOIN result_study std "
            + "ON std_id = pat_std_id "
            + "INNER JOIN map_study_rule msr "
            + "ON (std_name = msr_study_code) "
            + "WHERE msr.msr_id = #{datasetId}")
    @Results(value = {
            @Result(property = "id", column = "cer_id"),
            @Result(property = "subjectId", column = "cer_pat_id"),
            @Result(property = "startDate", column = "cer_start_date"),
            @Result(property = "term", column = "cer_term"),
            @Result(property = "aeNumber", column = "cer_ae_num"),
            @Result(property = "eventType", column = "cer_event_type"),
            @Result(property = "primaryIschemicStroke", column = "CER_PRIM_ISCHEMIC_STROKE"),
            @Result(property = "traumatic", column = "CER_TRAUMATIC"),
            @Result(property = "intraHemorrhageLoc", column = "CER_LOC_INTRA_HEMORRHAGE"),
            @Result(property = "intraHemorrhageOtherLoc", column = "CER_LOC_INTRA_HEMORRHAGE_OTH"),
            @Result(property = "symptomsDuration", column = "CER_SYMPTOMS_DURATION"),
            @Result(property = "mrsPriorToStroke", column = "CER_MRS_PRIOR_TO_STROKE"),
            @Result(property = "mrsDuringStrokeHosp", column = "CER_MRS_STROKE_HOSPITAL"),
            @Result(property = "mrsCurrVisitOr90dAfter", column = "CER_MRS_CUR_VISIT_90D_AFTER"),
            @Result(property = "comment", column = "CER_COMMENT")
    })
    @Options(fetchSize = 5000)
    List<CerebrovascularRaw> getRawData(@Param("datasetId") long datasetId);
}
