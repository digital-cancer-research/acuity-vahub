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
import com.acuity.visualisations.rawdatamodel.vo.AeRaw;
import com.acuity.visualisations.rawdatamodel.vo.AeSeverityEventCategoryValue;
import com.acuity.visualisations.rawdatamodel.vo.EventCategoryValue;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@AcuityRepository
public interface AeRepository extends RawDataRepository<AeRaw> {
    
   @Select(""
           + "SELECT DISTINCT ae_id                                                AS id,"
           + "  ae_id                                                              AS ae_id,"
           + "  aes_id                                                             AS severity_id,"
           + "  pat_id                                                             AS subject_id,"
           + "  evt_id                                                             AS evt_id,"
           + "  evt_pt                                                             AS evt_pt,"
           + "  evt_hlt                                                            AS evt_hlt,"
           + "  evt_llt                                                            AS evt_llt,"
           + "  evt_soc                                                            AS evt_soc,"
           + "  ae_text                                                            AS text,"
           + "  ae_comment                                                         AS ae_comment,"
           + "  1                                                                  AS calc_duration_if_null,"
           + "  COALESCE(MAGV_NAME, COALESCE(MAGR_DEFAULT_VALUE, 'Default group')) AS custom,"
           + "  ae_serious                                                         AS serious,"
           + "  cgl_lookup                                                         AS max_ctc,"
           + "  cgl_webapp_lookup                                                  AS webapp_max_ctc,"
           + "  cgl_numeric                                                        AS num_max_ctc,"
           + "  aes_start_date                                                     AS start_date,"
           + "  aes_end_date                                                       AS end_date,"
           + "  aes_ongoing                                                        AS ongoing,"
           + "  aes_end_type                                                       AS end_type,"
           + "  ae_outcome                                                         AS outcome,"
           + "  ae_dose_limiting_toxicity                                          AS dose_limiting_toxicity,"
           + "  ae_time_point                                                      AS time_point,"
           + "  ae_immune_mediated                                                 AS immune_mediated,"
           + "  ae_infusion_reaction                                               AS infusion_reaction,"
           + "  ae_required_treatment                                              AS required_treatment,"
           + "  ae_caused_subject_withdrawal                                       AS caused_subject_withdrawal,"
           + "  ae_suspected_endpoint                                              AS suspected_endpoint,"
           + "  ae_suspected_endpoint_category                                     AS suspected_endpoint_cat,"
           + "  ae_number                                                          AS ae_number,"
           + "  ae_of_special_interest                                             AS ae_of_special_interest"
           + ""
           + " FROM result_ae_severity aes"
           + "  INNER JOIN result_ae ae ON ae_id = aes_ae_id"
           + "  INNER JOIN result_event_type ON (ae_evt_id = evt_id)"
           + "  INNER JOIN result_patient pat ON (ae_pat_id = pat_id)"
           + "  INNER JOIN result_study std ON (pat_std_id = std_id)"
           + "  LEFT JOIN util_ctcg_lookup ON (upper(aes_severity) = cgl_label)"
           + "  LEFT JOIN result_death ON dth_pat_id = pat_id"
           + "  INNER JOIN map_study_rule ON (std_name = msr_study_code)"
           + "  LEFT JOIN map_study_ae_group ON (msag_msr_id = msr_id)"
           + "  LEFT JOIN map_ae_group_rule ON (msag_ae_group_id = MAGR_ID)"
           + "  LEFT JOIN map_ae_group_value_rule ON (MAGR_ID = magv_group_id AND trim(upper(EVT_PT)) = trim(upper(MAGV_PT)))"
           + "  WHERE pat_ip_dose_first_date IS NOT NULL  "
           + "  AND msr_id = #{datasetId}")
   @ResultMap("aeRaw")
   @Options(fetchSize = 5000)
   @Override
   List<AeRaw> getRawData(@Param("datasetId") long datasetId);

   @Select("SELECT "
           + " DISTINCT ae_id AS aeId, aes_id AS severityId, drug_name AS category, aeat_action_taken AS value"
           + " FROM result_ae aes "
           + " INNER JOIN result_ae_severity ON (aes_ae_id = ae_id) "
           + " INNER JOIN result_ae_action_taken ON (aeat_aes_id = aes_id) "
           + " INNER JOIN result_drug ON (drug_id = aeat_drug_id) "
           + " INNER JOIN result_patient pat ON pat_id = ae_pat_id "
           + " INNER JOIN result_study std ON std_id = pat_std_id "
           + " INNER JOIN map_study_rule msr ON (std_name = msr_study_code) "
           + " WHERE msr.msr_id = #{datasetId}")
   @Options(fetchSize = 1000)
   List<AeSeverityEventCategoryValue> getDistinctDrugsActionTaken(@Param("datasetId") long datasetId);

   @Select("SELECT "
           + " DISTINCT ae_id AS eventId, drug_name AS category, aec_causality AS value"
           + " FROM result_ae aes "
           + " INNER JOIN result_ae_causality ON (aec_ae_id = ae_id) "
           + " INNER JOIN result_drug ON drug_id = aec_drug_id "
           + " INNER JOIN result_patient pat ON pat_id = ae_pat_id "
           + " INNER JOIN result_study std ON std_id = pat_std_id "
           + " INNER JOIN map_study_rule msr ON (std_name = msr_study_code) "
           + " WHERE msr.msr_id = #{datasetId}")
   @Options(fetchSize = 1000)
   List<EventCategoryValue> getDistinctDrugsCausality(@Param("datasetId") long datasetId);
}
