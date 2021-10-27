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

import com.acuity.visualisations.common.study.metadata.MetadataItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;

import java.io.Serializable;
import java.util.HashMap;

import static com.google.common.collect.Lists.newArrayList;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;

/**
 *
 * @author Glen
 */
public class WhenMergingMetadatItems {

    @Test
    public void shouldBuildAndMergeMetadataItems() {

        MetadataItem miRight = new MetadataItem("labs");
        miRight.add("listOfUser", newArrayList("glen", "sam"));
        miRight.add("labsList", newArrayList("a", "b"));
        miRight.add("labsIntList", newArrayList(1, 2));
        miRight.add("labsDeomltList", newArrayList(1.1, 2.2));
        miRight.addProperty("string1", "string");
        miRight.addProperty("hasData", true);
        miRight.addProperty("count", 11);
        miRight.addProperty("countDeicmal", 44.34);

        MetadataItem miLeft = new MetadataItem("labs");
        miLeft.add("listOfUser", newArrayList("glen", "jim"));
        miLeft.add("labsList", newArrayList("c", "d"));
        miLeft.addProperty("string1", "another");
        miLeft.addProperty("hasData", false);
        miLeft.addProperty("count", 111);
        miLeft.addProperty("countFloat", 111.0);

        MetadataItem mergedItem = MetadataItem.merge(false, miLeft.build(), miRight.build());

        String json = mergedItem.build();
        System.out.println(json);

        assertThatJson(json).node("labs").isPresent();
        assertThatJson(json).node("labs.listOfUser").isEqualTo(newArrayList("glen", "jim", "sam"));
        assertThatJson(json).node("labs.labsList").isEqualTo(newArrayList("c", "d", "a", "b"));
        assertThatJson(json).node("labs.labsIntList").isEqualTo(newArrayList(1, 2));
        assertThatJson(json).node("labs.labsDeomltList").isEqualTo(newArrayList(1.1, 2.2));
        assertThatJson(json).node("labs.countFloat").isEqualTo(111.0);
        assertThatJson(json).node("labs.string1").isEqualTo("another");
        assertThatJson(json).node("labs.countDeicmal").isEqualTo(44.34);
        assertThatJson(json).node("labs.count").isAbsent();
        assertThatJson(json).node("labs.hasData").isAbsent();
    }

    @Data
    @AllArgsConstructor
    public class Patient implements Serializable {

        private String patientId;
        private String subjectCode;
    }

    @Test
    public void shouldBuildAndMergeMetadataItemsObject() {

        Patient patient1 = new Patient("Dummy1959-001", "Dummy1959-001");
        Patient patient2 = new Patient("Dummy200-001", "Dummy200-001");
                
        MetadataItem miRight = new MetadataItem("labs");
        miRight.add("patients", newArrayList(patient1));

        MetadataItem miLeft = new MetadataItem("labs");
        miLeft.add("patients", newArrayList(patient2));

        MetadataItem mergedItem = MetadataItem.merge(false, miLeft.build(), miRight.build());

        String json = mergedItem.build();
        System.out.println(json);

        assertThatJson(json).node("labs").isPresent();
        assertThatJson(json).node("labs.patients").isEqualTo(newArrayList(patient2, patient1));
    }
    
    @Test
    public void shouldBuildAndMergeMetadataItemsObject2() {

        Patient patient1 = new Patient("Dummy1959-001", "Dummy1959-001");
        Patient patient2 = new Patient("Dummy1959-001", "Dummy1959-001");
                
        MetadataItem miRight = new MetadataItem("labs");
        miRight.add("patients", newArrayList(patient1));

        MetadataItem miLeft = new MetadataItem("labs");
        miLeft.add("patients", newArrayList(patient2));

        MetadataItem mergedItem = MetadataItem.merge(false, miLeft.build(), miRight.build());

        String json = mergedItem.build();
        System.out.println(json);

        assertThatJson(json).node("labs").isPresent();
        assertThatJson(json).node("labs.patients").isEqualTo(newArrayList(patient1));
    }
    @Test
    public void shouldBuildAndMergeMetadataItemsObject3() {

        Patient patient1 = new Patient("Dummy1959-001", "Dummy1959-001");
        Patient patient2 = new Patient("Dummy1959-001", "Dummy1959-001");

        MetadataItem miRight = new MetadataItem("labs");
        miRight.add("patients", newArrayList(patient1));
        final HashMap map = new HashMap();
        map.put("key", "value");
        miRight.add("detailsOnDemandTitledColumns", map);

        MetadataItem miLeft = new MetadataItem("labs");
        miLeft.add("patients", newArrayList(patient2));

        MetadataItem mergedItem = MetadataItem.merge(true, miLeft.build(), miRight.build());

        String json = mergedItem.build();
        System.out.println(json);

        assertThatJson(json).node("labs").isPresent();
        assertThatJson(json).node("labs.patients").isEqualTo(newArrayList(patient1));
        assertThatJson(json).node("labs.detailsOnDemandTitledColumns").isEqualTo(map);
    }
}
