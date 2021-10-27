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
import com.acuity.va.security.acl.domain.DrugProgramme;
import com.acuity.va.security.acl.domain.vasecurity.DrugProgrammeInfo;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.acuity.visualisations.rawdatamodel.dataset.info.InfoRepository.LIST_ALL_DATASETS;

/**
 * @author Glen
 */
@AcuityRepository
@Transactional(readOnly = true)
public interface DrugProgrammeRepository {

    /**
     * High-level information of the drug programme for a drugProgramName
     */
    @Select("SELECT DISTINCT mpr_drug_display_name AS drug_project, mpr_creation_date AS added_date, mpr_created_by AS added_by "
            + " FROM ("
            + LIST_ALL_DATASETS
            + " ) as all_datasets"
            + " WHERE mpr_drug_display_name = #{name} ")
    @Results(value = {
        @Result(property = "drugProgramme", column = "drug_project"),
        @Result(property = "addedDate", column = "added_date"),
    })
    DrugProgrammeInfo getDrugProgrammeInfo(DrugProgramme drugProgramme);

    /**
     * List all drug programme names
     */
    @Select("SELECT DISTINCT mpr_drug_display_name AS drug_project "
            + " FROM ("
            + LIST_ALL_DATASETS
            + " ) AS all_datasets")
    List<String> listDrugProgrammes();

    /**
     * List all studies for the drugProgrammeName
     */
    @Select("SELECT DISTINCT msr_study_code "
            + " FROM ("
            + LIST_ALL_DATASETS
            + " ) "
            + " WHERE mpr_drug = #{name}")
    List<String> listStudies(DrugProgramme drugProgramme);
}
