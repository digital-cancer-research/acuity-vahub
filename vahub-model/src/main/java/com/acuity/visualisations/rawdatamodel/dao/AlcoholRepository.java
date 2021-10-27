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
import com.acuity.visualisations.rawdatamodel.vo.AlcoholRaw;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@AcuityRepository
public interface AlcoholRepository extends RawDataRepository<AlcoholRaw> {
    @Select("SELECT DISTINCT ASU_ID     AS event_id, "
            + "  ASU_PAT_ID             AS subject_id, "
            + "  ASU_CATEGORY           AS substance_category, "
            + "  ASU_USE_OCCUR          AS substance_use_occurrence, "
            + "  ASU_TYPE               AS substanceType, "
            + "  ASU_OTH_TYPE_SPEC      AS other_substance_type_spec, "
            + "  ASU_CONSUMPTION        AS substance_consumption, "
            + "  ASU_FREQ               AS frequency, "
            + "  ASU_START_DATE         AS start_date, "
            + "  ASU_END_DATE           AS end_date, "
            + "  ASU_TYPE_USE_OCCUR     AS substance_type_use_occur "
            + "FROM RESULT_ALCOHOL_SUB_USE "
            + "INNER JOIN RESULT_PATIENT "
            + "ON ASU_PAT_ID = PAT_ID "
            + "INNER JOIN RESULT_STUDY "
            + "ON STD_ID = PAT_STD_ID "
            + "INNER JOIN map_study_rule "
            + "ON msr_study_code = std_name "
            + "WHERE msr_id = #{datasetId}")
    @Results({
            @Result(property = "id", column = "event_id"),
            @Result(property = "subjectId", column = "subject_id"),
            @Result(property = "substanceCategory", column = "substance_category"),
            @Result(property = "substanceUseOccurrence", column = "substance_use_occurrence"),
            @Result(property = "substanceType", column = "substanceType"),
            @Result(property = "otherSubstanceTypeSpec", column = "other_substance_type_spec"),
            @Result(property = "substanceConsumption", column = "substance_consumption"),
            @Result(property = "frequency", column = "frequency"),
            @Result(property = "substanceTypeUseOccurrence", column = "substance_type_use_occur"),
            @Result(property = "startDate", column = "start_date"),
            @Result(property = "endDate", column = "end_date")
    })
    @Options(fetchSize = 5000)
    List<AlcoholRaw> getRawData(@Param("datasetId") long datasetId);
}
