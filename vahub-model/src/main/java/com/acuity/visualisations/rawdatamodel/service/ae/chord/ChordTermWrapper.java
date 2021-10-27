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

package com.acuity.visualisations.rawdatamodel.service.ae.chord;

import com.acuity.visualisations.rawdatamodel.vo.plots.ChordContributor;
import com.acuity.visualisations.rawdatamodel.vo.plots.ChordCalculationObject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Getter
final class ChordTermWrapper {

    private Map<Ae.TermLevel, Map<String, ChordCalculationObject>> chords;

    public static ChordTermWrapper empty() {
        return new ChordTermWrapper();
    }

    private ChordTermWrapper() {
        chords = new HashMap<>();
        chords.put(Ae.TermLevel.PT, new HashMap<>());
        chords.put(Ae.TermLevel.HLT, new HashMap<>());
        chords.put(Ae.TermLevel.SOC, new HashMap<>());
    }

    void populateWith(Ae.TermLevel level, String term1, String term2, int width, Map<String, List<ChordContributor>> contributors) {
        // key is alphabetically sorted concatenated string of terms of two aes
        // for instance, HEADACHE and FEVER would give FEVERHEADACHE as a key
        if (Ae.TermLevel.PT.equals(level)) {
            chords.get(level).put(term1 + term2, new ChordCalculationObject(term1, term2, width, contributors));
        } else if (!term1.equals(term2)) {
            List<String> terms = Stream.of(term1, term2).sorted().collect(toList());
            chords.get(level).merge(terms.get(0) + terms.get(1),
                    new ChordCalculationObject(terms.get(0), terms.get(1), width, contributors),
                    ChordCalculationObject::merge);
        }
    }
}
