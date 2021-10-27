package com.acuity.visualisations.rawdatamodel.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode
public class MetadataCacheKey {
    private Class<?> entity;
    private Column.DatasetType datasetType;
    private Column.Type columnType;
}
