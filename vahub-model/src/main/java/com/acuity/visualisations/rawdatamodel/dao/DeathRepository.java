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
import com.acuity.visualisations.rawdatamodel.vo.DeathRaw;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@AcuityRepository
public interface DeathRepository extends RawDataRepository<DeathRaw> {
    @Select("SELECT DISTINCT DTH_ID       AS event_id, "
            + "  DTH_PAT_ID               AS subject_id, "
            + "  DTH_CAUSE                AS death_cause, "
            + "  DTH_DATE                 AS death_date, "
            + "  DTH_RELATED_INV_DISEASE  AS disease_under_investigation, "
            + "  DTH_AUTOPSY_PERFORMED    AS autopsy_performed, "
            + "  DTH_DESIGNATION_OF_CAUSE AS designation, "
            + "  DTH_HLT                  AS hlt, "
            + "  DTH_LLT                  AS llt, "
            + "  DTH_PREFERRED_TERM       AS preferred_term, "
            + "  DTH_SOC                  AS soc "
            + "FROM RESULT_DEATH "
            + "INNER JOIN RESULT_PATIENT "
            + "ON DTH_PAT_ID = PAT_ID "
            + "INNER JOIN RESULT_STUDY "
            + "ON STD_ID = PAT_STD_ID "
            + "INNER JOIN map_study_rule "
            + "ON msr_study_code = std_name "
            + "WHERE msr_id      = #{datasetId}")
    @Results({
            @Result(property = "id", column = "event_id"),
            @Result(property = "subjectId", column = "subject_id"),
            @Result(property = "deathCause", column = "death_cause"),
            @Result(property = "dateOfDeath", column = "death_date"),
            @Result(property = "autopsyPerformed", column = "autopsy_performed"),
            @Result(property = "designation", column = "designation"),
            @Result(property = "diseaseUnderInvestigationDeath", column = "disease_under_investigation"),
            @Result(property = "hlt", column = "hlt"),
            @Result(property = "llt", column = "llt"),
            @Result(property = "preferredTerm", column = "preferred_term"),
            @Result(property = "soc", column = "soc")
    })
    @Options(fetchSize = 5000)
    List<DeathRaw> getRawData(@Param("datasetId") long datasetId);
}

