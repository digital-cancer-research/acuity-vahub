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
import com.acuity.visualisations.rawdatamodel.vo.CtDnaRaw;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@AcuityRepository
public interface CtDnaRepository extends RawDataRepository<CtDnaRaw> {
    @Select("SELECT DISTINCT "
            + "  ctd_id                         AS event_id, "
            + "  ctd_pat_id                     AS subject_id, "
            + "  ctd_gene                       AS gene, "
            + "  ctd_mutation                   AS mutation, "
            + "  ctd_tracked_mutation           AS tracked_mutation, "
            + "  ROUND(ctd_reported_var_allele_freq, 3) AS reported_vaf, "
            + "  ROUND(ctd_reported_var_allele_freq * 100, 2) AS reported_vaf_percent, "
            + "  ctd_sample_date                AS sample_date, "
            + "  ctd_visit_name                 AS visit_name, "
            + "  ctd_visit_number               AS visit_number "
            + "FROM result_ctdna INNER JOIN result_patient ON ctd_pat_id = pat_id "
            + "  INNER JOIN result_study ON pat_std_id = std_id "
            + "  INNER JOIN map_study_rule ON msr_study_code = std_name "
            + "WHERE msr_id = #{datasetId}")
    @Results({
            @Result(property = "id", column = "event_id"),
            @Result(property = "subjectId", column = "subject_id"),
            @Result(property = "gene", column = "gene"),
            @Result(property = "mutation", column = "mutation"),
            @Result(property = "trackedMutation", column = "tracked_mutation"),
            @Result(property = "reportedVaf", column = "reported_vaf"),
            @Result(property = "reportedVafPercent", column = "reported_vaf_percent"),
            @Result(property = "sampleDate", column = "sample_date"),
            @Result(property = "visitName", column = "visit_name"),
            @Result(property = "visitNumber", column = "visit_number")
    })
    @Options(fetchSize = 5000)
    List<CtDnaRaw> getRawData(@Param("datasetId") long datasetId);
}
