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
import com.acuity.visualisations.rawdatamodel.vo.SecondTimeOfProgressionRaw;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @deprecated looks like second time of progression entities and all related code are obsolete; it should be checked and probably removed
 */
@AcuityRepository
public interface SecondTimeOfProgressionRepository extends RawDataRepository<SecondTimeOfProgressionRaw> {
    @Select("SELECT DISTINCT '1'        AS subject_id,"
            + " '2'                     AS event_id"
            + " FROM result_patient"
            + " JOIN result_study ON std_id = pat_std_id"
            + " JOIN map_study_rule msr ON msr_study_code = std_name"
            + " WHERE pat_id = '1' AND msr.msr_id  = #{datasetId}")
    @Results({
            @Result(property = "id", column = "event_id"),
            @Result(property = "subjectId", column = "subject_id"),
    })
    @Options(fetchSize = 5000)
    List<SecondTimeOfProgressionRaw> getRawData(@Param("datasetId") long datasetId);
}
