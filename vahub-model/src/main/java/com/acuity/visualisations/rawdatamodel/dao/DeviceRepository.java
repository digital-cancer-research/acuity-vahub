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
import com.acuity.visualisations.rawdatamodel.vo.Device;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@AcuityRepository
public interface DeviceRepository extends RawDataRepository<Device> {
    @Select({"select src_id, src_name, src_type, src_version"
            + " from RESULT_SOURCE"
            + " where SRC_ID in "
            + "(select lab_src_id from RESULT_LABORATORY where lab_tst_id in "
            + "(select tst_id from RESULT_TEST inner join RESULT_PATIENT on tst_pat_id = pat_id"
            + " inner join result_study on std_id = pat_std_id "
            + " inner join map_study_rule ON (std_name = msr_study_code) "
            + " where msr_id = #{datasetId}))"})
    @Results(value = {
            @Result(property = "id", column = "src_id"),
            @Result(property = "name", column = "src_name"),
            @Result(property = "version", column = "src_version"),
            @Result(property = "type", column = "src_type"),
    })
    @Options(fetchSize = 5000)
    List<Device> getRawData(@Param("datasetId") long datasetId);
}
