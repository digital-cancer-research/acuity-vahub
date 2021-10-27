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
import com.acuity.visualisations.rawdatamodel.service.event.AeService;
import com.acuity.visualisations.rawdatamodel.service.event.LabService;
import com.acuity.visualisations.rawdatamodel.vo.LiverRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Liver;
import com.acuity.va.security.acl.domain.Datasets;
import com.google.common.collect.ImmutableSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Service
public class LiverModuleMetadata extends AbstractModuleMetadata<LiverRaw, Liver> {

    private static final Set<String> JUMP_TO_AE_SOCS = ImmutableSet.of("hepatobiliary disorders", "hepat");

    private static final Set<String> JUMP_TO_ACUITY_LABS = ImmutableSet.of(
            "alanine aminotransferase", "alkaline phosphatase", "aspartate aminotransferase",
            "total bilirubin", "bilirubin");

    @Autowired
    private AeService aeService;

    @Autowired
    private LabService labService;

    @Override
    protected String tab() {
        return "liver";
    }

    @Override
    protected MetadataItem buildMetadataItem(MetadataItem metadataItem, Datasets datasets) {
        Collection<String> labNames;
        if (datasets.isAcuityType()) {
            labNames = labService.getJumpToLabs(datasets, JUMP_TO_ACUITY_LABS);
        } else {
            labNames = Collections.emptyList();
        }
        Set<String> socs = aeService.getJumpToAesSocs(datasets, JUMP_TO_AE_SOCS);

        return super.buildMetadataItem(metadataItem, datasets)
                .add("labNames", labNames)
                .add("socs", socs);
    }

    @Override
    protected MetadataItem buildErrorMetadataItem(MetadataItem metadataItem) {
        return super.buildErrorMetadataItem(metadataItem)
                .add("labNames", Collections.emptyList())
                .add("socs", Collections.emptyList());
    }
}
