package com.acuity.visualisations.rawdatamodel.service.ssv;

import com.acuity.va.security.acl.domain.Datasets;


public interface ColorInitializer {
    default void generateColors(Datasets datasets) { }
}
