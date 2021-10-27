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

package com.acuity.visualisations.common.filter;

import com.acuity.visualisations.common.study.metadata.InstanceMetadata;
import com.acuity.visualisations.common.study.metadata.MetadataItem;
import org.junit.Test;

import java.util.HashMap;

import static com.google.common.collect.Lists.newArrayList;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;

/**
 *
 * @author Glen
 */
public class WhenMergingInstanceMetadatItems {

    @Test
    public void shouldBuildAndReadInstanceMetadataWithoutCounts() {

        MetadataItem miLeft1 = new MetadataItem("aes");
        miLeft1.add("listOfUser", newArrayList("glen", "sam"));
        miLeft1.add("aesList", newArrayList("b", "d"));
        miLeft1.addProperty("stringg", "stringa");
        miLeft1.addProperty("hasData", true);
        miLeft1.add("detailsOnDemandTitledColumns", new HashMap<>());
        miLeft1.addProperty("hasDataTrue", true);
        miLeft1.addProperty("count", 11);

        MetadataItem miLeft2 = new MetadataItem("labs");
        miLeft2.add("listOfUser", newArrayList("glen", "sam1"));
        miLeft2.add("labsList", newArrayList("glen1", "sam1"));

        MetadataItem miRight1 = new MetadataItem("aes");
        miRight1.add("listOfUser", newArrayList("glen", "sam1"));
        miRight1.add("aesList", newArrayList("a", "c"));
        miRight1.addProperty("stringg", "stringb");
        miRight1.addProperty("hasData", false);
        miRight1.add("detailsOnDemandTitledColumns", new HashMap<>());
        miRight1.addProperty("hasDataTrue", true);
        miRight1.addProperty("count", 12);

        MetadataItem miRight2 = new MetadataItem("labs");
        miRight2.add("listOfUser", newArrayList("glen1", "sam1"));
        miRight2.add("labsList", newArrayList("glen1", "sam"));

        InstanceMetadata instanceMetadataLeft = new InstanceMetadata();
        instanceMetadataLeft.add(miLeft1);
        instanceMetadataLeft.add(miLeft2);

        InstanceMetadata instanceMetadataRight = new InstanceMetadata();
        instanceMetadataRight.add(miRight1);
        instanceMetadataRight.add(miRight2);

        InstanceMetadata mergedInstanceMetadata = InstanceMetadata.merge(false, instanceMetadataLeft.build(), instanceMetadataRight.build());

        String json = mergedInstanceMetadata.build();
        System.out.println(json);

        assertThatJson(json).node("labs").isPresent();
        assertThatJson(json).node("labs.listOfUser").isEqualTo(newArrayList("glen", "sam1", "glen1"));
        assertThatJson(json).node("labs.labsList").isEqualTo(newArrayList("glen1", "sam1", "sam"));
        assertThatJson(json).node("labs.detailsOnDemandTitledColumns").isAbsent();
        assertThatJson(json).node("labs.count").isAbsent();
        assertThatJson(json).node("labs.hasData").isAbsent();

        assertThatJson(json).node("aes").isPresent();
        assertThatJson(json).node("aes.listOfUser").isEqualTo(newArrayList("glen", "sam", "sam1"));
        assertThatJson(json).node("aes.aesList").isEqualTo(newArrayList("b", "d", "a", "c"));
        assertThatJson(json).node("aes.hasDataTrue").isEqualTo(true);
        assertThatJson(json).node("aes.stringg").isEqualTo("stringa");
        assertThatJson(json).node("aes.detailsOnDemandTitledColumns").isAbsent();
        assertThatJson(json).node("aes.count").isAbsent();
        assertThatJson(json).node("aes.hasData").isAbsent();
    }

    @Test
    public void shouldBuildAndReadCountInstanceMetadataWithCounts() {

        MetadataItem miLeft1 = new MetadataItem("aes");
        miLeft1.add("availableXAxisOptions", newArrayList("X1"));
        miLeft1.add("detailsOnDemandColumns", newArrayList("D1", "D2"));
        miLeft1.addProperty("hasData", true);
        miLeft1.addProperty("count", 11);

        MetadataItem miRight1 = new MetadataItem("aes");
        miRight1.add("availableXAxisOptions", newArrayList("glen", "sam1"));
        miRight1.add("detailsOnDemandColumns", newArrayList("a", "c"));
        miRight1.addProperty("hasData", false);
        miRight1.addProperty("count", 12);

        MetadataItem miCounts = new MetadataItem("aes");
        miCounts.addProperty("hasData", true);
        miCounts.addProperty("count", 12);

        InstanceMetadata instanceMetadataLeft = new InstanceMetadata();
        instanceMetadataLeft.add(miLeft1);

        InstanceMetadata instanceMetadataRight = new InstanceMetadata();
        instanceMetadataRight.add(miRight1);

        InstanceMetadata instanceMetadataCounts = new InstanceMetadata();
        instanceMetadataCounts.add(miCounts);

        InstanceMetadata mergedInstanceMetadata = InstanceMetadata.merge(false, instanceMetadataLeft.build(), instanceMetadataRight.build());
        InstanceMetadata mergedCountsInstanceMetadata = InstanceMetadata.merge(true, mergedInstanceMetadata.build(), instanceMetadataCounts.build());

        String json = mergedInstanceMetadata.build();
        System.out.println(json);

        assertThatJson(json).node("aes").isPresent();
        assertThatJson(json).node("aes.availableXAxisOptions").isEqualTo(newArrayList("X1", "glen", "sam1"));
        assertThatJson(json).node("aes.detailsOnDemandColumns").isEqualTo(newArrayList("D1", "D2", "a", "c"));
        assertThatJson(json).node("aes.count").isAbsent();
        assertThatJson(json).node("aes.hasData").isAbsent();
        
        String countsjson = mergedCountsInstanceMetadata.build();
        System.out.println(countsjson);

        assertThatJson(countsjson).node("aes").isPresent();
        assertThatJson(countsjson).node("aes.availableXAxisOptions").isEqualTo(newArrayList("X1", "glen", "sam1"));
        assertThatJson(countsjson).node("aes.detailsOnDemandColumns").isEqualTo(newArrayList("D1", "D2", "a", "c"));
        assertThatJson(countsjson).node("aes.count").isEqualTo(12);
        assertThatJson(countsjson).node("aes.hasData").isEqualTo(true);
    }
}
