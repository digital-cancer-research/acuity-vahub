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

package com.acuity.visualisations.rawdatamodel.dataset.info;

import com.acuity.visualisations.common.lookup.AcuityRepository;
import com.acuity.visualisations.rawdatamodel.vo.StudyRules;
import com.acuity.va.security.acl.domain.DrugProgramme;
import com.acuity.va.security.acl.domain.AcuityDataset;
import com.acuity.va.security.acl.domain.AcuityObjectIdentity;
import org.apache.ibatis.annotations.Case;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.TypeDiscriminator;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Glen
 */
@AcuityRepository
@Transactional(readOnly = true)
public interface InfoRepository {

    /**
     * Union for all studies for reuse.
     */
    String FROM_ALL_DATASETS
            = "  FROM map_study_rule "
            + "   JOIN map_project_rule ON map_project_rule.mpr_id = map_study_rule.msr_prj_id"
            + "   LEFT JOIN map_clinical_study ON (mcs_study_id = msr_mcs_study_id AND mcs_mpr_id = msr_prj_id) "
            + "   WHERE msr_enabled = 1 AND mcs_study_name IS NOT NULL";

    String LIST_ALL_DATASETS
            = "SELECT * FROM map_study_rule "
            + " JOIN map_project_rule ON map_project_rule.mpr_id = map_study_rule.msr_prj_id "
            + " LEFT JOIN map_clinical_study ON (mcs_study_id = msr_mcs_study_id AND mcs_mpr_id = msr_prj_id)";

    /*
     * Lists all the datasets with their name, type and parent drug programme and study.
     * <p/>
     * Will return for example:
     * <p/>
     * <code>
     * Ie ID   Name            Type                                                           moduleType    defaultVisualisation   drugProgramme
     * 14   D2610C0003B        com.acuity.va.security.acl.domain.DetectDataset  detect   newWindow              CAZ-AVI
     * 21   D2610C00223        com.acuity.va.security.acl.domain.DetectDataset  detect   newWindow              CAZ-AVI
     * </code>
     */
    @Select("  SELECT DISTINCT"
            + "  map_project_rule.mpr_id as id,"
            + "  map_project_rule.mpr_drug_display_name as name,              "
            + "  1 AS orderIndex, "
            + "  'com.acuity.va.security.acl.domain.DrugProgramme' AS type, "
            + "   null as defaultVisualisation, "
            + "   null as clinicalStudyName, "
            + "   null as clinicalStudyCode, "
            + "   null as drugProgramme "
            + FROM_ALL_DATASETS
            + " UNION "
            + " (SELECT "
            + "  msr_id AS id, "
            + "  msr_study_name As name, "
            + "  3 AS orderIndex, "
            + "  'com.acuity.va.security.acl.domain.AcuityDataset' AS type, "
            + "  null as defaultVisualisation, "
            + "  mcs_study_name AS clinicalStudyName,"
            + "  mcs_study_id AS clinicalStudyCode,"
            + "  map_project_rule.mpr_drug_display_name as drugProgramme"
            + FROM_ALL_DATASETS
            + ")")
    @TypeDiscriminator(column = "type",
            cases = {
                @Case(value = "com.acuity.va.security.acl.domain.AcuityDataset", type = AcuityDataset.class),
                @Case(value = "com.acuity.va.security.acl.domain.DrugProgramme", type = DrugProgramme.class)
            })
    List<AcuityObjectIdentity> listObjectIdentities();

    @Select({"SELECT msr_limit_x_axis_to_visit FROM map_study_rule WHERE msr_id = #{studyId}"})
    @Results(
            value = {
                @Result(property = "limitXAxisToVisit", column = "msr_limit_x_axis_to_visit")
            }
    )
    StudyRules getStudyRules(@Param("studyId") Long studyId);
}
