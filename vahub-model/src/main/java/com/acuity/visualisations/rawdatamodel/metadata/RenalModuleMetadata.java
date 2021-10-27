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

package com.acuity.visualisations.rawdatamodel.metadata;

import com.acuity.visualisations.common.study.metadata.MetadataItem;
import com.acuity.visualisations.rawdatamodel.axes.ResultType;
import com.acuity.visualisations.rawdatamodel.service.event.AeService;
import com.acuity.visualisations.rawdatamodel.service.event.LabService;
import com.acuity.visualisations.rawdatamodel.vo.RenalRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Renal;
import com.acuity.va.security.acl.domain.Datasets;
import com.google.common.collect.ImmutableSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static com.acuity.visualisations.rawdatamodel.axes.CountType.PERCENTAGE_OF_SUBJECTS_100_PERCENT_STACKED;

@Service
public class RenalModuleMetadata extends AbstractModuleMetadata<RenalRaw, Renal> {

    public static final Set<String> AES_EVT_SOC = ImmutableSet.of("renal and urinary disorders", "renal");
    public static final Set<String> ACUITY_LAB_CODES = ImmutableSet.of("urea nitrogen", "creatinine", "creatinine clearance");
    public static final Set<String> DETECT_LAB_CODES = ImmutableSet.of("urean", "creat", "creatclr", "c25747", "curean", "screat", "bcreatcl");

    @Autowired
    private LabService labService;
    @Autowired
    private AeService aeService;

    @Override
    protected String tab() {
        return "renal-java";
    }

    @Override
    protected MetadataItem buildMetadataItem(MetadataItem metadataItem, Datasets datasets) {
        MetadataItem metadataItem1 = super.buildMetadataItem(metadataItem, datasets);
        metadataItem1.add("yAxisOptionsForBoxPlot", Arrays.asList(
                ResultType.ACTUAL_VALUE,
                ResultType.ABSOLUTE_CHANGE_FROM_BASELINE,
                ResultType.PERCENTAGE_CHANGE_FROM_BASELINE,
                ResultType.TIMES_UPPER_REF_VALUE,
                ResultType.TIMES_LOWER_REF_VALUE,
                ResultType.REF_RANGE_NORM_VALUE));
        metadataItem1.add("socs", aeService.getJumpToAesSocs(datasets, AES_EVT_SOC));
        metadataItem1.add("labCodes", datasets.isAcuityType() ? labService.getJumpToLabs(datasets, ACUITY_LAB_CODES)
                : labService.getJumpToNormalizedLabs(datasets, DETECT_LAB_CODES));
        metadataItem1.add("availableYAxisOptionsForCKDBarChart", Collections.singletonList(PERCENTAGE_OF_SUBJECTS_100_PERCENT_STACKED));

        int size = getData(datasets).size();
        metadataItem.addProperty("count", size);
        metadataItem.addProperty("hasData", size > 0);
        return metadataItem1;
    }

    @Override
    protected MetadataItem buildErrorMetadataItem(MetadataItem metadataItem) {
        super.buildErrorMetadataItem(metadataItem);
        metadataItem.add("yAxisOptionsForBoxPlot", Collections.emptyList());
        metadataItem.add("socs", Collections.emptyList());
        metadataItem.add("labCodes", Collections.emptyList());
        metadataItem.add("availableYAxisOptionsForCKDBarChart", Collections.emptyList());
        return metadataItem;
    }
}
