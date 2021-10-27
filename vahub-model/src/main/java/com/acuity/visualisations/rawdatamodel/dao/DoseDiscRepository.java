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
import com.acuity.visualisations.rawdatamodel.vo.DoseDiscRaw;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@AcuityRepository
public interface DoseDiscRepository extends RawDataRepository<DoseDiscRaw> {
    @Select("SELECT DISTINCT dsc_id      AS event_id, "
            + "  dsc_pat_id              AS subject_id, "
            + "  dsc_drug_name           AS study_drug, "
            + "  dsc_ipdc_date           AS ip_disc_date, "
            + "  dsc_ipdc_reas           AS ip_disc_reason, "
            + "  dsc_ipdc_spec           AS ip_disc_spec, "
            + "  dsc_subj_dec_spec       AS subject_decision_spec, "
            + "  dsc_subj_dec_spec_other AS subject_decision_spec_other   "
            + "FROM result_target_med_dos_disc "
            + "JOIN result_patient "
            + "ON dsc_pat_id = pat_id "
            + "JOIN result_study "
            + "ON pat_std_id = std_id "
            + "JOIN map_study_rule "
            + "ON msr_study_code = std_name "
            + "WHERE msr_id      = #{datasetId}")
    @Results(value = {
            @Result(property = "id", column = "event_id"),
            @Result(property = "subjectId", column = "subject_id"),
            @Result(property = "studyDrug", column = "study_drug"),
            @Result(property = "discDate", column = "ip_disc_date"),
            @Result(property = "discReason", column = "ip_disc_reason"),
            @Result(property = "ipDiscSpec", column = "ip_disc_spec"),
            @Result(property = "subjectDecisionSpec", column = "subject_decision_spec"),
            @Result(property = "subjectDecisionSpecOther", column = "subject_decision_spec_other")
    })
    @Options(fetchSize = 5000)
    List<DoseDiscRaw> getRawData(@Param("datasetId") long datasetId);
}
