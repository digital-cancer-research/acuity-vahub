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
import com.acuity.visualisations.rawdatamodel.vo.DiseaseExtentRaw;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@AcuityRepository
public interface DiseaseExtentRepository extends RawDataRepository<DiseaseExtentRaw> {
    @Select("SELECT de_id               AS event_id, "
            + "  de_pat_id              AS subject_id, "
            + "  de_meta_loc_adv        AS meta_loc_adv, "
            + "  de_site_local_meta_dis AS site_disease, "
            + "  de_visit_date          AS visit_date, "
            + "  de_rec_prgs_date       AS recent_progression, "
            + "  de_recu_earl_cancer    AS rec_of_earlier_cancer, "
            + "  de_oth_loc_adv_sites   AS other_loc_adv_sites, "
            + "  de_oth_meta_sites      AS other_metastatic_sites "
            + "FROM result_disease_extent "
            + "JOIN result_patient "
            + "ON de_pat_id = pat_id "
            + "JOIN result_study "
            + "ON std_id = pat_std_id "
            + "INNER JOIN map_study_rule "
            + "ON msr_study_code = std_name "
            + "WHERE msr_id      = #{datasetId}")
    @Results(value = {
            @Result(property = "id", column = "event_id"),
            @Result(property = "subjectId", column = "subject_id"),
            @Result(property = "siteLocalMetaDisease", column = "site_disease"),
            @Result(property = "localOrMetastaticCancer", column = "meta_loc_adv"),
            @Result(property = "recentProgressionDate", column = "recent_progression"),
            @Result(property = "recurrenceOfEarlierCancer", column = "rec_of_earlier_cancer"),
            @Result(property = "visitDate", column = "visit_date"),
            @Result(property = "otherLocAdvSites", column = "other_loc_adv_sites"),
            @Result(property = "otherMetastaticSites", column = "other_metastatic_sites")
    })
    @Options(fetchSize = 5000)
    List<DiseaseExtentRaw> getRawData(@Param("datasetId") long datasetId);
}
