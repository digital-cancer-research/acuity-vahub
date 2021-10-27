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

package com.acuity.visualisations.rawdatamodel.trellis;

import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TrellisOption<T, G extends Enum<G> & GroupByOption<T>> implements Serializable {
    private G trellisedBy;
    private Object trellisOption;

    @Override
    public String toString() {
        return trellisOption == null ? "(EMPTY)" : trellisOption.toString();
    }

    public static <T, G extends Enum<G> & GroupByOption<T>> TrellisOption<T, G> of(G trellisedBy, Object trellisOption) {
        return new TrellisOption<T, G>(trellisedBy, trellisOption);
    }
}
