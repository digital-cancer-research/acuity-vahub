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
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 *
 * @author Glen
 */
public class WhenWorkingWithMetadatItems {

    @Test
    public void shouldBuildAndReadMetadataItem() {

        MetadataItem mi = new MetadataItem("labs");
        mi.add("listOfUser", newArrayList("glen", "sam"));
        mi.add("labsList", newArrayList("glen", "sam"));
        mi.addProperty("string1", "string");
        mi.addProperty("hasData", true);
        mi.addProperty("count", 11);

        MetadataItem readItem = MetadataItem.read(mi.build());

        assertThat(readItem).isEqualToComparingFieldByField(mi);
    }
}
