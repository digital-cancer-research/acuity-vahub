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
