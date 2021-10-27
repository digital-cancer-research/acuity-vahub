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

package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.common.lookup.AcuityRepository;
import com.acuity.visualisations.rawdatamodel.dao.api.RawDataRepository;
import com.acuity.visualisations.rawdatamodel.vo.DrugDoseRaw;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@AcuityRepository
public interface DrugDoseLegacyRepository extends RawDataRepository<DrugDoseRaw> {
    @SuppressWarnings("checkstyle:OperatorWrap")
    @Select("WITH " +
            "    demo AS ( " +
            "        SELECT std_name                          AS pdm_dataset, " +
            "               subj.PAT_ID                            AS pdm_pat_id " +
            "        FROM result_patient subj " +
            "                 JOIN result_study study " +
            "                      ON std_id = pat_std_id " +
            "        WHERE std_name               = #{datasetName} " +
            "          AND (PAT_IP_DOSE_FIRST_DATE IS NOT NULL " +
            "            OR EXISTS " +
            "                   (SELECT 1 " +
            "                    FROM RESULT_EXACERBATION " +
            "                    WHERE RESULT_EXACERBATION.EXA_PAT_ID = pat_id " +
            "                   )) " +
            "    ), " +
            "    doses AS " +
            "        (SELECT " +
            "             pdm_pat_id           AS pat_id, " +
            "             mds_drug             AS drug, " +
            "             mds_start_date       AS sd, " +
            "             mds_end_date         AS ed, " +
            "             mds_dosing_freq_rank AS freq_rank, " +
            "             MDS_DOSING_FREQ_NAME AS freq, " +
            "             MDS_DOSE_UNIT        AS dose_unit, " +
            "             mds_dose             AS dose " +
            "         FROM result_trg_med_dos_schedule dosing " +
            "                  INNER JOIN demo " +
            "                             ON pdm_pat_id = mds_pat_id " +
            "        ), " +
            "    dscs AS " +
            "        (SELECT " +
            "             pdm_pat_id    AS pat_id, " +
            "             DSC_DRUG_NAME AS drug, " +
            "             DSC_IPDC_DATE AS dsc_date " +
            "         FROM RESULT_TARGET_MED_DOS_DISC dsc " +
            "                  INNER JOIN demo " +
            "                             ON pdm_pat_id = DSC_PAT_ID " +
            "        ), " +
            "     prepared_doses as ( " +
            "             SELECT pat_id, " +
            "                     drug, " +
            "                     sd, " +
            "                     CASE " +
            "                         WHEN dose > 0 " +
            "                             THEN 'ACTIVE' " +
            "                         ELSE 'INACTIVE' " +
            "                         END AS pt, " +
            "                     case when dose > 0 then dose else 0 end as dose, " +
            "                     case when dose > 0 then dose_unit else null end as dose_unit, " +
            "                     case when dose > 0 then freq else null end as freq, " +
            "                     case when dose > 0 then freq_rank else 0 end as freq_rank " +
            "              FROM doses " +
            "     ), " +
            "     enriched as ( " +
            "         SELECT doses.pat_id, " +
            "                doses.drug, " +
            "                doses.ed, " +
            "                acuity.nvl2(dscs.pat_id, 'DISCONTINUED', 'INACTIVE'), " +
            "                0, " +
            "                NULL, " +
            "                NULL, " +
            "                0 " +
            "         FROM doses " +
            "                  LEFT JOIN dscs " +
            "                            ON doses.pat_id = dscs.pat_id " +
            "                                AND doses.drug  = dscs.drug " +
            "                                AND doses.ed    = dscs.dsc_date " +
            "         where ed is not null " +
            "     ), " +
            "    ticks AS " +
            "        ( SELECT DISTINCT t.*, " +
            "                 lead(sd) over (partition BY pat_id, drug order by sd, pt ASC) AS ed " +
            "                 FROM( " +
            "               select * from prepared_doses " +
            "               UNION " +
            "                   select * from enriched " +
            "               UNION " +
            "               SELECT pat_id, drug, dsc_date, 'DISCONTINUED', 0, NULL, NULL, 0 FROM dscs " +
            "              ) t " +
            "          ORDER BY pat_id, " +
            "                   drug, " +
            "                   sd " +
            "        ), " +
            "    doses_new_interval_mark AS " +
            "        (SELECT DISTINCT sd, " +
            "                         ed, " +
            "                         pat_id, " +
            "                         drug, " +
            "                         pt, " +
            "                         dose, " +
            "                         dose_unit, " +
            "                         freq, " +
            "                         freq_rank, " +
            "                         CASE " +
            "                             WHEN MAX(ed)" +
            " over (PARTITION BY pat_id, drug, pt, dose, dose_unit, freq_rank " +
            " ORDER BY sd ROWS BETWEEN UNBOUNDED PRECEDING AND 1 PRECEDING ) >= sd " +
            "                                 THEN 0 " +
            "                             ELSE 1 " +
            "                             END AS new_interval " +
            "         FROM ticks " +
            "        ), " +
            "    doses_interval_rank AS " +
            "        (SELECT d.*, " +
            "                SUM(new_interval)" +
            " over (PARTITION BY pat_id, drug ORDER BY sd, pt ASC" +
            " ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS interval_rank " +
            "         FROM doses_new_interval_mark d " +
            "        ), " +
            "    periods AS " +
            "        ( SELECT DISTINCT pat_id AS ppat_id, " +
            "                          drug, " +
            "                          pt, " +
            "                          dose, " +
            "                          dose_unit, " +
            "                          freq, " +
            "                          freq_rank, " +
            "                          MIN(sd) over (partition BY d.pat_id, drug, interval_rank)             AS sd, " +
            "                          MAX(ed) over (partition BY d.pat_id, drug, interval_rank)             AS ed " +
            "          FROM doses_interval_rank d " +
            "        ) " +
            "SELECT " +
            "ppat_id                                               AS pdos_pat_id, " +
            "drug                                                  AS pdos_drug,         " +
            "cast (sd as timestamp)                                                    AS pdos_start_date,   " +
            "cast (COALESCE(ed, STD_DATE_LAST_UPLOADED) as timestamp)                  AS pdos_end_date,     " +
            "pt                                                    AS pdos_period_type,  " +
            "cast (dose as numeric(8))                             AS pdos_dose,         " +
            "dose_unit                                             AS pdos_dose_unit,    " +
            "freq                                                  AS pdos_freq,         " +
            "cast(freq_rank as varchar)                            AS pdos_freq_rank     " +
            "FROM periods " +
            "         INNER JOIN RESULT_PATIENT p " +
            "                    ON ppat_id = PAT_ID " +
            "         INNER JOIN RESULT_STUDY " +
            "                    ON STD_ID = PAT_STD_ID " +
            "ORDER BY pat_id, " +
            "         drug, " +
            "         sd, " +
            "         pt ")
    @Results(value = {
            @Result(property = "subjectId", column = "subject_id"),
            @Result(property = "drug", column = "pdos_drug"),
            @Result(property = "startDate", column = "pdos_start_date"),
            @Result(property = "endDate", column = "pdos_end_date"),
            @Result(property = "periodType", column = "pdos_period_type"),
            @Result(property = "dose", column = "pdos_dose"),
            @Result(property = "doseUnit", column = "pdos_dose_unit"),
            @Result(property = "frequencyName", column = "pdos_freq"),
            @Result(property = "frequencyRank", column = "pdos_freq_rank")
    })
    @Options(fetchSize = 5000)
    List<DrugDoseDatasetsDataProvider.DrugDoseIntermediate> getSqlPrecalculatedData(@Param("datasetName") String datasetName);
}
