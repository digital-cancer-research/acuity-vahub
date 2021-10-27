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
import com.acuity.visualisations.rawdatamodel.vo.CvotEndpointRaw;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@AcuityRepository
public interface CvotEndpointRepository extends RawDataRepository<CvotEndpointRaw> {
    @Select("SELECT cv.*, std.std_id, pat.pat_part, msr_study_code "
            + " from RESULT_CVOT cv "
            + " INNER JOIN result_patient pat "
            + " ON pat_id=cvot_pat_id  "
            + " INNER JOIN result_study std "
            + " ON std_id = pat_std_id "
            + " INNER JOIN map_study_rule msr "
            + " ON (std_name = msr_study_code) "
            + " WHERE msr.msr_id = #{datasetId}")
    @Results(value = {
            @Result(property = "id", column = "CVOT_ID"),
            @Result(property = "subjectId", column = "CVOT_PAT_ID"),
            @Result(property = "aeNumber", column = "CVOT_AE_NUM"),
            @Result(property = "startDate", column = "CVOT_START_DATE"),
            @Result(property = "term", column = "CVOT_TERM"),
            @Result(property = "category1", column = "CVOT_CATEGORY1"),
            @Result(property = "category2", column = "CVOT_CATEGORY2"),
            @Result(property = "category3", column = "CVOT_CATEGORY3"),
            @Result(property = "description1", column = "CVOT_DESCRIPTION1"),
            @Result(property = "description2", column = "CVOT_DESCRIPTION2"),
            @Result(property = "description3", column = "CVOT_DESCRIPTION3")

    })
    @Options(fetchSize = 5000)
    List<CvotEndpointRaw> getRawData(@Param("datasetId") long datasetId);
}
