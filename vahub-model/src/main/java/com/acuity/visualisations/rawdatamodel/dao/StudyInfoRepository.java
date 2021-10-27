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
import com.acuity.visualisations.rawdatamodel.vo.StudyInfo;
import com.acuity.visualisations.rawdatamodel.vo.StudyInfoAdministrationDetail;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;

@AcuityRepository
public interface StudyInfoRepository extends RawDataRepository<StudyInfo> {
    @Select("SELECT STD_DATE_LAST_UPLOADED as lastUploadedDate, "
            + " mpr_drug AS drugProject,"
            + " std_last_event_date AS lastEventDate,"
            + " msr_blinded AS blinded,"
            + " msr_randomised AS randomised,"
            + " msr_regulatory AS regulatory"
            + " FROM result_study"
            + " INNER JOIN"
            + " MAP_STUDY_RULE"
            + " ON std_name = msr_study_code"
            + " INNER JOIN map_project_rule ON mpr_id = msr_prj_id"
            + " WHERE msr_id  = #{datasetId}")
    @Results({
            @Result(property = "lastUpdatedDate", column = "lastUploadedDate"),
    })
    List<StudyInfo> getRawData(@Param("datasetId") long datasetId);

    @Select("<script>"
            + "SELECT MSR_ID, MSR_STUDY_CODE, MSR_CBIO_PROFILE_STUDY_CODE, MSR_AML_ENABLED "
            + " FROM MAP_STUDY_RULE "
            + " WHERE msr_id IN "
            + " (<foreach collection='datasetId' item='datasetId' separator=','>#{datasetId}</foreach>)"
            + "</script>")
    @Results({
            @Result(property = "studyId", column = "MSR_ID"),
            @Result(property = "studyCode", column = "MSR_STUDY_CODE"),
            @Result(property = "cBioStudyCode", column = "MSR_CBIO_PROFILE_STUDY_CODE"),
            @Result(property = "amlEnabled", column = "MSR_AML_ENABLED"),
    })
    List<StudyInfoAdministrationDetail> getStudyInfoByDatasetIds(@Param("datasetId") Collection<Long> datasetIds);
}
