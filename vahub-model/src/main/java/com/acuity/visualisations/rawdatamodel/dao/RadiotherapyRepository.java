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
import com.acuity.visualisations.rawdatamodel.vo.RadiotherapyRaw;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@AcuityRepository
public interface RadiotherapyRepository extends RawDataRepository<RadiotherapyRaw> {
    @Select("SELECT DISTINCT rad_pat_id  AS subject_id,"
            + "  rad_id                  AS event_id,"
            + "  rad_start_date          AS start_date,"
            + "  rad_end_date            AS end_date,"
            + "  rad_visit               AS visit,"
            + "  rad_visit_dat           AS visit_date,"
            + "  rad_given               AS given,"
            + "  rad_site_or_region      AS site_or_region,"
            + "  rad_treatment_status    AS treatment_status,"
            + "  rad_radiation_dose      AS dose,"
            + "  rad_number_of_doses     AS number_of_doses,"
            + "  rad_time_status         AS time_status"
            + " FROM result_radiotherapy"
            + " JOIN result_patient ON pat_id = rad_pat_id"
            + " JOIN result_study ON std_id = pat_std_id"
            + " JOIN map_study_rule msr ON msr_study_code = std_name"
            + " WHERE msr.msr_id  = #{datasetId}")
    @Results({
            @Result(property = "id", column = "event_id"),
            @Result(property = "subjectId", column = "subject_id"),
            @Result(property = "startDate", column = "start_date"),
            @Result(property = "endDate", column = "end_date"),
            @Result(property = "visit", column = "visit"),
            @Result(property = "visitDate", column = "visit_date"),
            @Result(property = "siteOrRegion", column = "site_or_region"),
            @Result(property = "treatmentStatus", column = "treatment_status"),
            @Result(property = "dose", column = "dose"),
            @Result(property = "numOfDoses", column = "number_of_doses"),
            @Result(property = "timeStatus", column = "time_status")
    })
    @Options(fetchSize = 5000)
    List<RadiotherapyRaw> getRawData(@Param("datasetId") long datasetId);
}
