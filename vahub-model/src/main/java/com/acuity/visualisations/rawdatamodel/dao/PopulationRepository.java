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
import com.acuity.visualisations.rawdatamodel.vo.GroupType;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.Subject.SubjectVisit;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@AcuityRepository
public interface PopulationRepository extends RawDataRepository<Subject> {

    @Select("SELECT DISTINCT "
            + "      mcs_study_id                                              AS clinical_study_code,"
            + "      mcs_study_name                                            AS clinical_study_name,"
            + "      msr_study_code                                            AS dataset_code,"
            + "      msr_study_name                                            AS dataset_name,"
            + "      std_name                                                  AS dataset,"
            + "      pat_subject                                               AS subject_id,"
            + "      pat_id                                                    AS event_id,"
            + "      pat_part                                                  AS study_part,"
            + "      pat_rand_date                                             AS date_of_randomisation,"
            + "      pat_withdrawal_date                                       AS date_of_withdrawal,"
            + "      pat_withdrawal_reason                                     AS reason_for_withdrawal,"
            + "      pat_ip_dose_first_date                                    AS first_treatment_date,"
            + "      pat_sex                                                   AS sex,"
            + "      pat_race                                                  AS race,"
            + "      pat_centre                                                AS center_number,"
            + "      pat_country                                               AS country,"
            + "      pat_baseline_date                                         AS baseline_date,"
            + "      std_date_last_uploaded                                    AS last_etl_date,"
            + "      coalesce(pat_withdrawal_date, std_date_last_uploaded)     AS study_leave_date,"
            + "      pat_birthdat                                              AS date_of_birth,"
            + "      pat_visdat                                                AS enroll_visit_date,"
            + "      pat_study_status                                          AS study_status"
            + " FROM result_patient "
            + " JOIN result_study ON std_id = pat_std_id "
            + " JOIN map_study_rule ON msr_study_code = std_name "
            + " JOIN map_project_rule ON mpr_id  = msr_prj_id "
            + " JOIN map_clinical_study ON msr_mcs_study_id = mcs_study_id AND mcs_mpr_id = msr_prj_id "
            + " WHERE msr_id = #{datasetId}"
            + " AND (PAT_IP_DOSE_FIRST_DATE IS NOT NULL OR EXISTS "
            + "     (SELECT 1 FROM RESULT_EXACERBATION WHERE RESULT_EXACERBATION.EXA_PAT_ID = pat_id))")
    @Results(value = {
            @Result(id = true, property = "subjectId", column = "event_id"),
            @Result(property = "subjectCode", column = "subject_id"),
            @Result(property = "clinicalStudyCode", column = "clinical_study_code"),
            @Result(property = "clinicalStudyName", column = "clinical_study_name"),
            @Result(property = "datasetCode", column = "dataset_code"),
            @Result(property = "datasetName", column = "dataset_name"),
            @Result(property = "datasetId", column = "dataset"),
            @Result(property = "studyPart", column = "study_part"),
            @Result(property = "dateOfRandomisation", column = "date_of_randomisation"),
            @Result(property = "firstTreatmentDate", column = "first_treatment_date"),
            @Result(property = "dateOfWithdrawal", column = "date_of_withdrawal"),
            @Result(property = "reasonForWithdrawal", column = "reason_for_withdrawal"),
            @Result(property = "sex", column = "sex"),
            @Result(property = "race", column = "race"),
            @Result(property = "centerNumber", column = "center_number"),
            @Result(property = "country", column = "country"),
            @Result(property = "lastEtlDate", column = "last_etl_date"),
            @Result(property = "studyLeaveDate", column = "study_leave_date"),
            @Result(property = "baselineDate", column = "baseline_date"),
            @Result(property = "dateOfBirth", column = "date_of_birth"),
            @Result(property = "enrollVisitDate", column = "enroll_visit_date"),
            @Result(property = "studyStatus", column = "study_status")
    })
    @Options(fetchSize = 5000)
    @Override
    List<Subject> getRawData(@Param("datasetId") long datasetId);

    @Select("SELECT vis_pat_id,"
            + "  vis_number, vis_date"
            + " FROM result_visit"
            + " INNER JOIN result_patient pat ON pat_id = vis_pat_id"
            + " JOIN result_study ON pat_std_id = std_id"
            + " JOIN map_study_rule ON std_name  = msr_study_code"
            + " WHERE msr_id = #{datasetId}"
            + " ORDER BY vis_pat_id, vis_number")
    @Results(value = {
            @Result(property = "subjectId", column = "vis_pat_id"),
            @Result(property = "visit", column = "vis_number"),
            @Result(property = "date", column = "vis_date")
    })
    @Options(fetchSize = 5000)
    List<SubjectVisit> getAttendedVisits(@Param("datasetId") long datasetId);

    @Select(" SELECT * FROM ( "
            + " SELECT COALESCE(pgr_grouping_name, 'Cohort')        AS gr_grouping_name, "
            + "         pat_id                                    AS gr_pat_id, "
            + "         msgt_type                                 AS gr_type, "
            + "         msga_index                                AS gr_index, "
            + "         msga_group_prefered_name                  AS gr_preferred_name, "
            + "         pgr_group_name                            AS gr_name, "
            + "         NULL                                      AS gr_default_name, "
            + "         msr_id                                    AS msr_id, "
            + "         msg_grouping_selected                     AS msg_grouping_selected "
            + "  FROM result_patient "
            + "  JOIN result_study ON pat_std_id = std_id "
            + "  JOIN map_study_rule ON std_name = msr_study_code "
            + "  LEFT JOIN result_patient_group ON pgr_pat_subject = pat_subject AND pgr_std_id = pat_std_id "
            + "  LEFT JOIN map_subject_grouping ON msg_study_id = msr_id AND msg_grouping_name = pgr_grouping_name "
            + "  LEFT JOIN map_subject_grouping_type ON msg_msgt_id = msgt_id "
            + "  LEFT JOIN map_subject_group_annotation ON msga_grouping_id = msg_id AND msga_group_name = pgr_group_name"
            + "  UNION "
            + "  SELECT COALESCE(msgr_name, 'Cohort') AS gr_grouping_name, "
            + "         pat_id                                    AS gr_pat_id, "
            + "         msgt_type                                 AS gr_type, "
            + "         msga_index                                AS gr_index, "
            + "         msga_group_prefered_name                  AS gr_preferred_name, "
            + "         msgv_name                                 AS gr_name, "
            + "         msgr_default_value                        AS gr_default_name, "
            + "         msr_id                                    AS msr_id, "
            + "         msg_grouping_selected                     AS msg_grouping_selected "
            + "  FROM result_patient "
            + "  JOIN result_study ON pat_std_id = std_id "
            + "  JOIN map_study_rule ON std_name = msr_study_code "
            + "  JOIN map_subject_group_rule ON msr_id = msgr_study_id "
            + "  LEFT JOIN map_subject_group_value_rule ON msgv_group_id = msgr_id AND msgv_subject_id = pat_subject "
            + "  LEFT JOIN map_subject_grouping ON msg_study_id = msr_id AND msg_grouping_name = msgr_name "
            + "  LEFT JOIN map_subject_grouping_type ON msg_msgt_id = msgt_id "
            + "  LEFT JOIN map_subject_group_annotation ON msga_grouping_id = msg_id AND msga_group_name = msgv_name "
            + ") groups "
            + "WHERE groups.msg_grouping_selected = 'true' AND groups.gr_type IN ('NONE', 'DOSE') AND groups.msr_id = #{datasetId}")
    @Results(value = {
            @Result(property = "subjectId", column = "gr_pat_id"),
            @Result(property = "groupingName", column = "gr_grouping_name"),
            @Result(property = "groupType", column = "gr_type", javaType = GroupType.class),
            @Result(property = "groupIndex", column = "gr_index"),
            @Result(property = "groupPreferredName", column = "gr_preferred_name"),
            @Result(property = "groupName", column = "gr_name"),
            @Result(property = "groupDefaultName", column = "gr_default_name")
    })
    @Options(fetchSize = 5000)
    List<Subject.SubjectGroup> getSubjectGroup(@Param("datasetId") long datasetId);

    @Select(" SELECT sc_pat_id                                    AS pat_id, "
            + "      sc_ethpop                                    AS ethnic_group, "
            + "      sc_s_ethpop                                  AS specified_ethnic_group "
            + "  FROM result_sc "
            + "  JOIN result_patient ON pat_id = sc_pat_id "
            + "  JOIN result_study ON std_id = pat_std_id "
            + "  JOIN map_study_rule msr ON msr_study_code = std_name "
            + "  WHERE msr.msr_id  = #{datasetId} ")
    @Results(value = {
            @Result(property = "subjectId", column = "pat_id"),
            @Result(property = "ethnicGroup", column = "ethnic_group"),
            @Result(property = "specifiedEthnicGroup", column = "specified_ethnic_group")
    })
    @Options(fetchSize = 5000)
    List<Subject.SubjectEthnicGroup> getSubjectEthnicGroup(@Param("datasetId") long datasetId);

    @Select(" SELECT tst_pat_id                                    AS vit_pat_id, "
            + "      vit_test_name                                 AS tst_name, "
            + "      tst_date                                      AS tst_date, "
            + "      vit_value                                     AS tst_value "
            + "  FROM result_vitals"
            + "  JOIN result_test ON (tst_id = vit_tst_id) "
            + "  JOIN result_patient ON pat_id = tst_pat_id"
            + "  JOIN result_study ON std_id = pat_std_id "
            + "  JOIN map_study_rule msr ON msr_study_code = std_name "
            + "  WHERE upper(vit_test_name) = 'WEIGHT' or upper(vit_test_name) = 'HEIGHT' and msr.msr_id  = #{datasetId} ")
    @Results(value = {
            @Result(property = "subjectId", column = "vit_pat_id"),
            @Result(property = "testName", column = "tst_name"),
            @Result(property = "testDate", column = "tst_date"),
            @Result(property = "testValue", column = "tst_value")
    })
    @Options(fetchSize = 5000)
    List<Subject.SubjectVitalsInfo> getSubjectVitalsInfo(@Param("datasetId") long datasetId);

    @Select("select pat_id as subject_id, "
            + "mh_term as reported_term "
            + "from result_medical_history mh "
            + "JOIN result_patient ON mh_pat_id = pat_id "
            + "JOIN result_study ON std_id = pat_std_id "
            + "JOIN map_study_rule ON msr_study_code = std_name "
            + "WHERE msr_id      = #{datasetId}")
    @Results(value = {
            @Result(property = "subjectId", column = "subject_id"),
            @Result(property = "reportedTerm", column = "reported_term")
    })
    @Options(fetchSize = 5000)
    List<Subject.SubjectMedicalHistories> getMedicalHistories(@Param("datasetId") long datasetId);

    /**
     * @deprecated looks like study specific filters and all related code are obsolete; it should be checked and probably removed
     */
    @Select("SELECT '' AS vis_pat_id, '' AS study_specific_filter WHERE 1=2")
    @Results(value = {
            @Result(property = "subjectId", column = "subject_id"),
            @Result(property = "studySpecificFilter", column = "study_specific_filter")
    })
    @Options(fetchSize = 5000)
    List<Subject.SubjectStudySpecificFilters> getSubjectStudySpecificFilters(@Param("datasetId") long datasetId);
}
