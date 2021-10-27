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
 import com.acuity.va.security.acl.domain.Dataset;
 import com.acuity.va.security.acl.domain.vasecurity.DatasetInfo;
 import org.apache.ibatis.annotations.Result;
 import org.apache.ibatis.annotations.Results;
 import org.apache.ibatis.annotations.Select;
 import org.springframework.transaction.annotation.Transactional;


 /**
  * map_study_rule.msr_study_name is the dataset name
  *
  * @author Glen
  */
 @AcuityRepository
 @Transactional(readOnly = true)
 public interface InfoDatasetRepository {

     /**
      * High-level information of the dataset for an id
      */
     @Select("SELECT DISTINCT "
             + "   msr_id AS va_security_id,"
             + "   msr_study_code AS study_id,"
             + "   msr_study_name AS study_name,"
             + "   msr_study_name AS dataset_name,"
             + "   msr_creation_date,"
             + "   mpr_drug_display_name AS drug_project,"
             + "   'acuity' AS moduleType "
             + " FROM map_study_rule "
             + " JOIN map_project_rule ON map_project_rule.mpr_id = map_study_rule.msr_prj_id "
             + " WHERE msr_id = #{id}")
     @Results(value = {
         @Result(property = "id", column = "va_security_id"),
         @Result(property = "name", column = "dataset_name"),
         @Result(property = "clinicalStudy", column = "study_id"),
         @Result(property = "drugProgramme", column = "drug_project"),
         @Result(property = "addedDate", column = "msr_creation_date"),
         @Result(property = "addedBy", column = "msr_created_by")
     })
     DatasetInfo getDatasetInfo(Dataset dataset);
 }
