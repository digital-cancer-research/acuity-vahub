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
import com.acuity.visualisations.rawdatamodel.vo.SeriousAeRaw;
import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

@AcuityRepository
public interface SeriousAeRepository extends RawDataRepository<SeriousAeRaw> {

    @Select("SELECT SAE_ID               AS event_id, "
            + "  SAE_PAT_ID              AS subject_id, "
            + "  SAE_ADVERSE_EVENT       AS ae, "
            + "  EVT_PT                  AS pt, "
            + "  SAE_PRIM_CAUSE_DEATH    AS primary_death_cause, "
            + "  SAE_SEC_CAUSE_DEATH     AS secondary_death_cause, "
            + "  SAE_OTHER_MED           AS other_medication, "
            + "  SAE_CAUSED_OTHER_MED    AS caused_by_other_medication, "
            + "  SAE_STD_PROCD           AS study_procedure, "
            + "  SAE_CAUSED_STD_PROCD    AS caused_by_study, "
            + "  SAE_AE_DESC             AS description, "
            + "  SAE_RES_IN_DEATH        AS result_in_death, "
            + "  SAE_REQR_HOSP           AS hospitalization_required, "
            + "  SAE_CONG_ANOM           AS congenital_anomaly, "
            + "  SAE_LIFE_THR            AS life_threatening, "
            + "  SAE_PERS_DISABILITY     AS disability, "
            + "  SAE_OTHER_EVENT         AS other_serious_event, "
            + "  SAE_ADD_DRUG            AS ad, "
            + "  SAE_CAUSED_BY_ADD_DRUG  AS caused_by_ad, "
            + "  SAE_ADD_DRUG1           AS ad1, "
            + "  SAE_CAUSED_BY_ADD_DRUG1 AS caused_by_ad1, "
            + "  SAE_ADD_DRUG2           AS ad2, "
            + "  SAE_CAUSED_BY_ADD_DRUG2 AS caused_by_ad2, "
            + "  SAE_CRIT_MET_DATE       AS become_serious_date, "
            + "  SAE_INV_AWARE_DATE      AS find_out_date, "
            + "  SAE_HOSP_DATE           AS hospitalization_date, "
            + "  SAE_DISCH_DATE          AS discharge_date, "
            + "  SAE_AE_NUM              AS num "
            + "FROM result_serious_adverse_event sae "
            + "INNER JOIN result_patient "
            + "ON sae_pat_id = pat_id "
            + "INNER JOIN result_study "
            + "ON PAT_STD_ID = std_id "
            + "INNER JOIN map_study_rule "
            + "ON msr_study_code = std_name "
            + "LEFT JOIN result_ae "
            + "ON AE_NUMBER  = SAE_AE_NUM "
            + "AND AE_PAT_ID = SAE_PAT_ID "
            + "LEFT JOIN result_event_type "
            + "ON EVT_ID    = AE_EVT_ID "
            + "WHERE msr_id = #{datasetId}")
    @Results({
            @Result(property = "id", column = "event_id"),
            @Result(property = "subjectId", column = "subject_id"),
            @Result(property = "ae", column = "ae"),
            @Result(property = "pt", column = "pt"),
            @Result(property = "primaryDeathCause", column = "primary_death_cause"),
            @Result(property = "secondaryDeathCause", column = "secondary_death_cause"),
            @Result(property = "otherMedication", column = "other_medication"),
            @Result(property = "causedByOtherMedication", column = "caused_by_other_medication"),
            @Result(property = "studyProcedure", column = "study_procedure"),
            @Result(property = "causedByStudy", column = "caused_by_study"),
            @Result(property = "description", column = "description"),
            @Result(property = "resultInDeath", column = "result_in_death"),
            @Result(property = "hospitalizationRequired", column = "hospitalization_required"),
            @Result(property = "congenitalAnomaly", column = "congenital_anomaly"),
            @Result(property = "lifeThreatening", column = "life_threatening"),
            @Result(property = "disability", column = "disability"),
            @Result(property = "otherSeriousEvent", column = "other_serious_event"),
            @Result(property = "ad", column = "ad"),
            @Result(property = "causedByAD", column = "caused_by_ad"),
            @Result(property = "ad1", column = "ad1"),
            @Result(property = "causedByAD1", column = "caused_by_ad1"),
            @Result(property = "ad2", column = "ad2"),
            @Result(property = "causedByAD2", column = "caused_by_ad2"),
            @Result(property = "ad", column = "ad"),
            @Result(property = "becomeSeriousDate", column = "become_serious_date"),
            @Result(property = "findOutDate", column = "find_out_date"),
            @Result(property = "hospitalizationDate", column = "hospitalization_date"),
            @Result(property = "dischargeDate", column = "discharge_date"),
            @Result(property = "num", column = "num")
    })
    @Options(fetchSize = 5000)
    List<SeriousAeRaw> getRawData(@Param("datasetId") long datasetId);

    @Select("SELECT SAE_ID               AS event_id, "
            + "  AES_START_DATE          AS start_date, "
            + "  AES_END_DATE            AS end_date "
            + "FROM result_serious_adverse_event sae "
            + "INNER JOIN result_patient "
            + "ON sae_pat_id = pat_id "
            + "INNER JOIN result_study "
            + "ON PAT_STD_ID = std_id "
            + "INNER JOIN map_study_rule "
            + "ON msr_study_code = std_name "
            + "LEFT JOIN result_ae "
            + "ON AE_NUMBER  = SAE_AE_NUM "
            + "AND AE_PAT_ID = SAE_PAT_ID "
            + "LEFT JOIN result_ae_severity "
            + "ON aes_ae_id = ae_id "
            + "WHERE msr_id = #{datasetId}")
    @ConstructorArgs({
            @Arg(column = "event_id", javaType = String.class),
            @Arg(column = "start_date", javaType = Date.class),
            @Arg(column = "end_date", javaType = Date.class)
    })
    @Options(fetchSize = 5000)
    List<SeriousAeRaw.SeriousAeSeverityStartEndDates> getAeSeverityDates(@Param("datasetId") long datasetId);
}
