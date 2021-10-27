package com.acuity.visualisations.rawdatamodel.dao;

import com.acuity.visualisations.common.lookup.AcuityRepository;
import com.acuity.visualisations.rawdatamodel.dao.api.RawDataRepository;
import com.acuity.visualisations.rawdatamodel.vo.NicotineRaw;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@AcuityRepository
public interface NicotineRawDataRepository extends RawDataRepository<NicotineRaw> {
    @Select("SELECT DISTINCT nsu_id, "
            + "              nsu_pat_id, "
            + "              nsu_category, "
            + "              nsu_use_occur, "
            + "              nsu_type, "
            + "              nsu_oth_type_spec, "
            + "              nsu_sub_type_use_occur, "
            + "              nsu_current_use_spec, "
            + "              nsu_use_start_date, "
            + "              nsu_use_end_date, "
            + "              nsu_consumption, "
            + "              nsu_use_freq_interval, "
            + "              nsu_num_pack_years "
            + "FROM result_nicotine_sub_use "
            + "INNER JOIN result_patient ON nsu_pat_id = pat_id "
            + "INNER JOIN result_study ON std_id = pat_std_id "
            + "INNER JOIN map_study_rule ON msr_study_code = std_name "
            + "WHERE msr_id      = #{datasetId}")
    @Results(value = {
            @Result(property = "id", column = "nsu_id"),
            @Result(property = "subjectId", column = "nsu_pat_id"),
            @Result(property = "category", column = "nsu_category"),
            @Result(property = "useOccurrence", column = "nsu_use_occur"),
            @Result(property = "type", column = "nsu_type"),
            @Result(property = "otherTypeSpec", column = "nsu_oth_type_spec"),
            @Result(property = "subTypeUseOccurrence", column = "nsu_sub_type_use_occur"),
            @Result(property = "currentUseSpec", column = "nsu_current_use_spec"),
            @Result(property = "startDate", column = "nsu_use_start_date"),
            @Result(property = "endDate", column = "nsu_use_end_date"),
            @Result(property = "consumption", column = "nsu_consumption"),
            @Result(property = "frequencyInterval", column = "nsu_use_freq_interval"),
            @Result(property = "numberPackYears", column = "nsu_num_pack_years")
    })
    @Options(fetchSize = 5000)
    List<NicotineRaw> getRawData(@Param("datasetId") long datasetId);
}
