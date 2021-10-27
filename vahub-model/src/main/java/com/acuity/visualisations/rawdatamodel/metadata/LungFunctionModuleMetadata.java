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
import com.acuity.visualisations.rawdatamodel.vo.LungFunctionRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LungFunction;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;

@Service
public class LungFunctionModuleMetadata extends AbstractModuleMetadata<LungFunctionRaw, LungFunction> {

    @Override
    protected String tab() {
        return "lungFunction-java";
    }

    @Override
    protected MetadataItem buildMetadataItem(MetadataItem metadataItem, Datasets datasets) {
        metadataItem = super.buildMetadataItem(metadataItem, datasets);
        metadataItem.add("availableYAxisOptions", Arrays.asList(
                ResultType.ACTUAL_VALUE,
                ResultType.ABSOLUTE_CHANGE_FROM_BASELINE,
                ResultType.PERCENTAGE_CHANGE_FROM_BASELINE
        ));

        return metadataItem;
    }

    @Override
    protected MetadataItem buildErrorMetadataItem(MetadataItem metadataItem) {
        metadataItem = super.buildErrorMetadataItem(metadataItem);
        metadataItem.add("availableYAxisOptions", Collections.emptyList());
        return metadataItem;
    }
}
