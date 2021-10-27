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

package com.acuity.visualisations.config.util;

import com.acuity.va.security.acl.domain.AcuityDataset;
import com.acuity.va.security.acl.domain.ClinicalStudy;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;
import com.acuity.va.security.acl.domain.DetectDataset;

/**
 * @author ksnd199
 */
public final class TestConstants {

    /**
     * Test studies
     */
    // Change these constants when you want to run the tests against different datasets.
    public static final Long DUMMY_DETECT_VA_ID = 401234744L;
    public static final Long DUMMY_DETECT_V3 = 401234704L;
    public static final Long DUMMY_SMALL_DETECT_VA_ID = 401234644L;
    public static final String DETECT_DUMMY_STUDY_ID = "DummyData05";
    public static final String DETECT_DUMMY_SMALL_STUDY_ID = "DummySmall";
    public static final String SUBJECT_ID_PREFIX = "DummyData05";
    public static final String ETHNIC_ASHANTI = "ashanti";
    public static final String ETHNIC_BONOMAN = "bonoman";
    public static final String DOSE_DISC = "dosedisc";
    public static final String SUBJECT_ID = "subjectId";
    public static final String DISC_REASON = "discReason";
    public static final String DOD_COLUMNS = "detailsOnDemandColumns";
    // Dummy
    public static final Long COMBINED_DUMMY_DETECT_VA_SEC_ID = 4012324525L;
    public static final String DETECT_COMBINED_DUMMY_STUDY_ID = "DummyCombined";
    public static final ClinicalStudy DUMMY_DETECT_STUDY = new ClinicalStudy(DUMMY_DETECT_VA_ID, DETECT_DUMMY_STUDY_ID);
    public static final Dataset DRUG_X_V3 = new DetectDataset(DUMMY_DETECT_V3);
    public static final Dataset DUMMY_DETECT_DATASET = new DetectDataset(DUMMY_DETECT_VA_ID, DETECT_DUMMY_STUDY_ID + " Dataset");
    public static final Dataset DUMMY_DETECT_DATASET_TST = new DetectDataset(DUMMY_DETECT_VA_ID, DETECT_DUMMY_STUDY_ID);
    // Dummy Small
    public static final Dataset DUMMY_SMALL_DETECT_DATASET = new DetectDataset(DUMMY_SMALL_DETECT_VA_ID, DETECT_DUMMY_SMALL_STUDY_ID + " Dataset");
    // Datasets
    public static final Datasets DUMMY_DETECT_DATASETS = new Datasets(DUMMY_DETECT_DATASET);
    public static final Datasets DUMMY_DETECT_DATASETS_TST = new Datasets(DUMMY_DETECT_DATASET_TST);
    public static final Datasets DUMMY_SMALL_DETECT_DATASETS = new Datasets(DUMMY_SMALL_DETECT_DATASET);
    public static final Datasets MULTI_DUMMY_DETECT_DATASETS = new Datasets(DUMMY_DETECT_DATASET, DUMMY_SMALL_DETECT_DATASET);
    public static final Datasets DRAG_X_V3_DATASETS = new Datasets(DRUG_X_V3);
    public static final ClinicalStudy COMBINED_DUMMY_DETECT_STUDY = new ClinicalStudy(COMBINED_DUMMY_DETECT_VA_SEC_ID, DETECT_COMBINED_DUMMY_STUDY_ID);
    public static final Dataset COMBINED_DUMMY_DETECT_DATASET = new DetectDataset(COMBINED_DUMMY_DETECT_VA_SEC_ID, DETECT_COMBINED_DUMMY_STUDY_ID + " Dataset");

    //  DUMMY_ACUITY_NICOTINE_DATASETS
    //  Acuity
    public static final Long DUMMY_SMALL_ACUITY_VA_ID = 819L;
    public static final Long DUMMY_2_ACUITY_VA_ID = 835L;
    public static final Long DUMMY_ACUITY_CARDIAC_VA_ID = 819L;
    // Following should be deprecated. Two IDs should be enough for all cases.
    @Deprecated
    public static final Long DUMMY_ACUITY_VA_ID = 819L;
    @Deprecated
    public static final Long DUMMY_ACUITY_LUNG_FUNC_VA_ID = 835L;
    @Deprecated
    public static final Long DUMMY_ACUITY_RECIST_VA_ID = 819L;
    @Deprecated
    public static final Long DUMMY_ACUITY_DEATH_VA_ID = 819L;
    @Deprecated
    public static final Long DUMMY_ACUITY_DOSE_VA_ID = 819L;
    @Deprecated
    public static final Long DUMMY_ACUITY_NICOTINE_VA_ID = 819L;
    @Deprecated
    public static final Long DUMMY_ACUITY_HISTORIES_VA_ID = 819L;
    @Deprecated
    public static final Long DUMMY_ACUITY_ALCOHOL_VA_ID = 819L;
    @Deprecated
    public static final Long DUMMY_ACUITY_LIVER_VA_ID = 819L;
    @Deprecated
    public static final Long DUMMY_ACUITY_LABS_VA_ID = 819L;
    @Deprecated
    public static final Long DUMMY_ACUITY_VA_ID_42 = 819L;
    @Deprecated
    public static final Long DUMMY_ACUITY_BIOMARKERS_ID = 819L;
    @Deprecated
    public static final Long DUMMY_ACUITY_CVOT_ID = 819L;
    @Deprecated
    public static final Long DUMMY_ACUITY_SPECIAL_INTEREST_GROUP_ID = 819L;
    @Deprecated
    public static final Long DUMMY_ACUITY_BISCAY_VA_ID = 819L;

    public static final Dataset DUMMY_ACUITY_DATASET = new AcuityDataset(DUMMY_ACUITY_VA_ID);
    public static final Dataset DUMMY_2_ACUITY_DATASET = new AcuityDataset(DUMMY_2_ACUITY_VA_ID);
    public static final Dataset DUMMY_SMALL_ACUITY_DATASET = new AcuityDataset(DUMMY_SMALL_ACUITY_VA_ID);
    public static final Dataset DUMMY_ACUITY_LUNG_FUNC_DATASET = new AcuityDataset(DUMMY_ACUITY_LUNG_FUNC_VA_ID);
    public static final Dataset DUMMY_ACUITY_RECIST_DATASET = new AcuityDataset(DUMMY_ACUITY_RECIST_VA_ID);
    public static final Dataset DUMMY_DEATH_DATASET = new AcuityDataset(DUMMY_ACUITY_DEATH_VA_ID);
    public static final Dataset DUMMY_ACUITY_DOSE_DATASET = new AcuityDataset(DUMMY_ACUITY_DOSE_VA_ID);
    public static final Dataset DUMMY_ACUITY_HISTORIES_DATASET = new AcuityDataset(DUMMY_ACUITY_HISTORIES_VA_ID);
    public static final Dataset DUMMY_LIVER_DATASET = new AcuityDataset(DUMMY_ACUITY_LIVER_VA_ID);
    public static final Dataset DUMMY_ACUITY_ALCOHOL_DATASET = new AcuityDataset(DUMMY_ACUITY_ALCOHOL_VA_ID);
    public static final Dataset DUMMY_ACUITY_NICOTINE_DATASET = new AcuityDataset(DUMMY_ACUITY_NICOTINE_VA_ID);
    public static final Dataset DUMMY_ACUITY_LABS_DATASET = new AcuityDataset(DUMMY_ACUITY_LABS_VA_ID);
    public static final Dataset DUMMY_ACUITY_DATASET_42 = new AcuityDataset(DUMMY_ACUITY_VA_ID_42);
    public static final Dataset DUMMY_ACUITY_BIOMARKERS_DATASET = new AcuityDataset(DUMMY_ACUITY_BIOMARKERS_ID);
    public static final Dataset DUMMY_ACUITY_CVOT_DATASET = new AcuityDataset(DUMMY_ACUITY_CVOT_ID);
    public static final Dataset DUMMY_ACUITY_CARDIAC_DATASET = new AcuityDataset(DUMMY_ACUITY_CARDIAC_VA_ID);
    public static final Dataset DUMMY_ACUITY_SPECIAL_INTEREST_GROUP_DATASET = new AcuityDataset(DUMMY_ACUITY_SPECIAL_INTEREST_GROUP_ID);

    public static final Datasets DUMMY_ACUITY_DATASETS = new Datasets(DUMMY_ACUITY_DATASET);
    public static final Datasets DUMMY_2_ACUITY_DATASETS = new Datasets(DUMMY_2_ACUITY_DATASET);
    public static final Datasets DUMMY_ACUITY_LUNG_FUNC_DATASETS = new Datasets(DUMMY_ACUITY_LUNG_FUNC_DATASET);
    public static final Datasets DUMMY_DEATH_DATASETS = new Datasets(DUMMY_DEATH_DATASET);
    public static final Datasets DUMMY_ACUITY_DOSE_DATASETS = new Datasets(DUMMY_ACUITY_DOSE_DATASET);
    public static final Datasets DUMMY_ACUITY_ALCOHOL_DATASETS = new Datasets(DUMMY_ACUITY_ALCOHOL_DATASET);
    public static final Datasets MULTI_DUMMY_ACUITY_DATASETS = new Datasets(DUMMY_ACUITY_DATASET, DUMMY_2_ACUITY_DATASET);
    public static final Datasets DUMMY_SMALL_ACUITY_DATASETS = new Datasets(DUMMY_SMALL_ACUITY_DATASET);
    public static final Datasets DUMMY_ACUITY_RECIST_DATASETS = new Datasets(DUMMY_ACUITY_RECIST_DATASET);
    public static final Datasets DUMMY_ACUITY_NICOTINE_DATASETS = new Datasets(DUMMY_ACUITY_NICOTINE_DATASET);
    public static final Datasets DUMMY_ACUITY_CARDIAC_DATASETS = new Datasets(DUMMY_ACUITY_CARDIAC_DATASET);
    public static final Datasets DUMMY_ACUITY_HISTORIES_DATASETS = new Datasets(DUMMY_ACUITY_HISTORIES_DATASET);
    public static final Datasets DUMMY_LIVER_DATASETS = new Datasets(DUMMY_LIVER_DATASET);
    public static final Datasets DUMMY_LABS_DATASETS = new Datasets(DUMMY_ACUITY_LABS_DATASET);
    public static final Datasets DUMMY_BIOMARKERS_DATASETS = new Datasets(DUMMY_ACUITY_BIOMARKERS_DATASET);
    public static final Datasets DUMMY_CVOT_DATASETS = new Datasets(DUMMY_ACUITY_CVOT_DATASET);
    public static final Datasets DUMMY_ACUITY_SPECIAL_INTEREST_GROUP_DATASETS = new Datasets(DUMMY_ACUITY_SPECIAL_INTEREST_GROUP_DATASET);

    public static final String DUMMY_DRUG_PROGRAMME_NAME = "Drug X";

    private TestConstants() {
    }
}
