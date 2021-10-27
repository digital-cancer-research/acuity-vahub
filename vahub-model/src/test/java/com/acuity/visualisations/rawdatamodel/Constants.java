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
