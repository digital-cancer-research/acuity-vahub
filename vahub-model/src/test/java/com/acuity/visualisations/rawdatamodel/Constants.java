package com.acuity.visualisations.rawdatamodel;

import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;
import com.acuity.va.security.acl.domain.DetectDataset;
import com.acuity.va.security.acl.domain.AcuityDataset;


public final class Constants {

    public static final Dataset DATASET = new AcuityDataset(42L);
    public static final Dataset CEREBRO_DATASET = new AcuityDataset(241L);
    public static final Dataset DETECT_DATASET = new DetectDataset(401234744L);
    public static final Datasets DATASETS = new Datasets(DATASET);
    public static final Datasets CEREBRO_DATASETS = new Datasets(CEREBRO_DATASET);
    public static final Datasets DETECT_DATASETS = new Datasets(DETECT_DATASET);

    private Constants() {

    }
}
