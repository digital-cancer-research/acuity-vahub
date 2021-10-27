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
import com.acuity.visualisations.rawdatamodel.vo.CIEventRaw;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@AcuityRepository
public interface CIEventRepository extends RawDataRepository<CIEventRaw> {
    @Select("SELECT ci.*, std.std_id, pat.pat_part "
            + "from RESULT_CI_EVENT ci "
            + "INNER JOIN result_patient pat ON pat_id=ci_pat_id "
            + "INNER JOIN result_study std ON std_id = pat_std_id "
            + "INNER JOIN map_study_rule msr ON (std_name = msr_study_code) "
            + "WHERE msr.msr_id = #{datasetId}")
    @Results(value = {
            @Result(property = "id", column = "ci_id"),
            @Result(property = "subjectId", column = "ci_pat_id"),
            @Result(property = "startDate", column = "ci_start_date"),
            @Result(property = "term", column = "ci_event_term"),
            @Result(property = "aeNumber", column = "ci_ae_num"),
            @Result(property = "ischemicSymptoms", column = "ci_ischemic_symptoms"),
            @Result(property = "cieSymptomsDuration", column = "ci_symptoms_duration"),
            @Result(property = "symptPromptUnschedHospit", column = "ci_symptoms_prompt_uns_hosp"),
            @Result(property = "eventSuspDueToStentThromb", column = "ci_event_due_to_stent_thromb"),
            @Result(property = "previousEcgAvailable", column = "ci_prev_ecg_available"),
            @Result(property = "previousEcgDate", column = "ci_prev_ecg_date"),
            @Result(property = "ecgAtTheEventTime", column = "ci_ecg_at_event_time"),
            @Result(property = "noEcgAtTheEventTime", column = "ci_no_ecg_at_event_time"),
            @Result(property = "localCardiacBiomarkersDrawn", column = "ci_local_card_biom_drawn"),
            @Result(property = "coronaryAngiography", column = "ci_coron_angiography_perf"),
            @Result(property = "angiographyDate", column = "ci_date_of_angiogr"),
            @Result(property = "finalDiagnosis", column = "ci_fin_diagnosis"),
            @Result(property = "otherDiagnosis", column = "ci_oth_diagnosis"),
            @Result(property = "description1", column = "ci_description1"),
            @Result(property = "description2", column = "ci_description2"),
            @Result(property = "description3", column = "ci_description3"),
            @Result(property = "description4", column = "ci_description4"),
            @Result(property = "description5", column = "ci_description5")
    })
    @Options(fetchSize = 5000)
    List<CIEventRaw> getRawData(@Param("datasetId") long datasetId);
}
