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

package com.acuity.visualisations.rawdatamodel.vo.compatibility;

import com.acuity.visualisations.rawdatamodel.trellis.TrellisOption;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrellisedOvertime<T, G extends Enum<G> & GroupByOption<T>> implements TrellisedChart<T, G>, Serializable {
    private List<TrellisOption<T, G>> trellisedBy;
    private OutputOvertimeData data;

    public static <T, G extends Enum<G> & GroupByOption<T>> TrellisedOvertime<T, G> of(GroupByKey<T, G> key, OutputOvertimeData data) {
        return new TrellisedOvertime<>(key.getTrellisByValues().entrySet().stream()
                .map(k -> TrellisOption.of(k.getKey(), k.getValue())).collect(Collectors.toList()), data);
    }
}
