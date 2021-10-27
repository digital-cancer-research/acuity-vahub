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

package com.acuity.visualisations.rawdatamodel.util;

import com.acuity.visualisations.rawdatamodel.vo.AeRaw;
import com.acuity.visualisations.rawdatamodel.vo.AeSeverityRaw;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;

/**
 *
 * @author ksnd199
 */
public final class AeRawTransformer {

    private AeRawTransformer() {
    }

    public static Collection<AeRaw> transformToSeverityChange(Collection<AeRaw> incidenceAes) {
        return transformToSeverityChange(incidenceAes, true);
    }

    /*
     * Transforms a list of Aes in incidence format to Severity Change format
     */
    public static Collection<AeRaw> transformToSeverityChange(Collection<AeRaw> incidenceAes, boolean parallel) {

        Collection<AeRaw> severityAes = new CopyOnWriteArrayList<>();

        Stream<AeRaw> aeRawStream = parallel ? incidenceAes.parallelStream() : incidenceAes.stream();

        //Glen, wouldn't it be easier here to simply do flatMap transformation?
        aeRawStream.forEach(iae -> {
            Stream<AeSeverityRaw> aeAeSeveritiesStream = parallel ? iae.getAeSeverities().parallelStream() : iae.getAeSeverities().stream();
            aeAeSeveritiesStream.forEach(aeSev -> {
                AeRaw newAeRaw = iae.toBuilder().
                        aeSeverities(newArrayList(aeSev)).
                        id(aeSev.getId()).// swap the id to the Aes Severity Id
                        build();

                severityAes.add(newAeRaw);
            });
        });

        return severityAes;
    }
}
