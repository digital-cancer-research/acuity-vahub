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

package com.acuity.visualisations.rawdatamodel.vo.plots;

import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Getter
@Setter
public class ChordCalculationObject implements Serializable {
    private String start;
    private String end;
    private int width;
    private Map<String, List<ChordContributor>> contributors;

    public ChordCalculationObject(String start, String end, int width, Map<String, List<ChordContributor>> contributors) {
        this.start = start;
        this.end = end;
        this.width = width;
        this.contributors = new HashMap<>(contributors);
    }

    public static ChordCalculationObject merge(ChordCalculationObject o1, ChordCalculationObject o2) {
        o1.setWidth(o1.getWidth() + o2.getWidth());
        Map<String, List<ChordContributor>> contributors1 = o1.getContributors();
        o2.getContributors().forEach((k, v) -> contributors1.merge(k, v, (v1, v2) -> Stream.of(v1, v2)
                .flatMap(Collection::stream)
                .collect(toList())));
        return o1;
    }

    public enum Attributes implements GroupByOption<ChordCalculationObject> {
        START(EntityAttribute.attribute("START", ChordCalculationObject::getStart)),
        END(EntityAttribute.attribute("END", ChordCalculationObject::getEnd));

        @Getter
        private final EntityAttribute<ChordCalculationObject> attribute;

        Attributes(EntityAttribute<ChordCalculationObject> attribute) {
            this.attribute = attribute;
        }
    }
}
