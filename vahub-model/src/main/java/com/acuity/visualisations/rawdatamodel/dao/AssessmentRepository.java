package com.acuity.visualisations.rawdatamodel.dao;

import com.acuity.visualisations.common.lookup.AcuityRepository;
import com.acuity.visualisations.rawdatamodel.dao.api.RawDataRepository;
import com.acuity.visualisations.rawdatamodel.vo.AssessmentRaw;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@AcuityRepository
public interface AssessmentRepository extends RawDataRepository<AssessmentRaw> {
    @Select("SELECT rca_id                                      AS event_id, "
            + "  rca_pat_id                                     AS subject_id, "
            + "  rca_assessment_date                            AS assessment_date, "
            + "  rca_visit_date                                 AS visit_date, "
            + "  rca_visit                                      AS visit_number, "
            + "  rca_new_les_site                               AS lesion_site, "
            + "  rca_new_les_since_baseline                     AS is_new_lesion, "
            + "  NULL                                           AS method, "
            + "  COALESCE(rrl_name, rca_recist_response)        AS response, "
            + "  COALESCE(rrl_shortname, rca_recist_response)   AS response_short, "
            + "  rrl_rank                                       AS rank, "
            + "  ROUND(rca_assess_freq)                         AS assessment_frequency "
            + "FROM RESULT_RECIST_ASSESSMENT "
            + "JOIN result_patient "
            + "ON rca_pat_id = pat_id "
            + "JOIN result_study "
            + "ON std_id = pat_std_id "
            + "JOIN map_study_rule "
            + "ON msr_study_code = std_name "
            + "LEFT JOIN util_recist_response_lookup "
            + "ON (((LOWER(rca_recist_response) ~ rrl_regex) "
            + "OR upper(RRL_SHORTNAME) = upper(rca_recist_response))) "
            + "WHERE msr_id      = #{datasetId}")
    @Results(value = {
            @Result(property = "id", column = "event_id"),
            @Result(property = "subjectId", column = "subject_id"),
            @Result(property = "assessmentDate", column = "assessment_date"),
            @Result(property = "visitDate", column = "visit_date"),
            @Result(property = "visitNumber", column = "visit_number"),
            @Result(property = "lesionSite", column = "lesion_site"),
            @Result(property = "newLesionSinceBaseline", column = "is_new_lesion"),
            @Result(property = "assessmentMethod", column = "method"),
            @Result(property = "response", column = "response"),
            @Result(property = "responseShort", column = "response_short"),
            @Result(property = "responseRank", column = "rank"),
            @Result(property = "assessmentFrequency", column = "assessment_frequency")
    })
    @Options(fetchSize = 5000)
    List<AssessmentRaw> getRawData(@Param("datasetId") long datasetId);
}
