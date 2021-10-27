package com.acuity.visualisations.rawdatamodel.util;

import com.acuity.va.security.acl.domain.Datasets;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ColorbyCategoriesUtil {
    private ColorbyCategoriesUtil() { }
    public static String getDatasetColorByOption(Datasets datasets, Object colorByOption) {
        String datasetColorByOption = Stream.of(Optional.ofNullable(datasets).map(Datasets::getIdsAsString),
                Optional.ofNullable(colorByOption.toString()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.joining("_"));
        return datasetColorByOption;
    }
}
