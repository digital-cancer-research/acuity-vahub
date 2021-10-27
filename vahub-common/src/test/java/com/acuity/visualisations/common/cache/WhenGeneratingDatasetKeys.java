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

package com.acuity.visualisations.common.cache;

import static com.acuity.visualisations.common.cache.DatasetsKeyGenerator.generateDatasetsKey;
import com.acuity.va.security.acl.domain.Datasets;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.springframework.cache.interceptor.SimpleKey;

/**
 *
 * @author ksnd199
 */
public class WhenGeneratingDatasetKeys {

    private DatasetsKeyGenerator datasetsKeyGenerator = new DatasetsKeyGenerator();

    @Test
    public void shouldResolveSingleDatasets() {
        Datasets datasets = Datasets.toDetectDataset(1);

        Object key = datasetsKeyGenerator.generate(this, null, new Object[]{datasets, "other"});

        assertThat(key).isInstanceOf(DatasetsKey.class);
        assertThat(((DatasetsKey) key).getDatasets()).isEqualTo(datasets);
    }

    @Test
    public void shouldResolveSingleNoneDatasets() {
        Object key = datasetsKeyGenerator.generate(this, null, new Object[]{"other"});

        assertThat(key).isInstanceOf(String.class);
    }

    @Test
    public void shouldResolveMultipleNoneDatasets() {
        Object key = datasetsKeyGenerator.generate(this, null, new Object[]{"other", "other1"});

        assertThat(key).isInstanceOf(SimpleKey.class);
    }

    @Test
    public void shouldCreateDatasetsKey() {
        Datasets datasets = Datasets.toDetectDataset(1);

        DatasetsKey key1 = generateDatasetsKey(datasets, "other1");
        DatasetsKey key2 = generateDatasetsKey(datasets, "other2");

        assertThat(key1).isNotEqualTo(key2);
        assertThat(key1.getDatasets()).isEqualTo(datasets);

        assertThat(DatasetsFinder.hasDatasets(new Object[]{datasets, "other2"})).isTrue();
        assertThat(DatasetsFinder.findDatasetsObject(new Object[]{datasets, "other2"})).isEqualTo(datasets);
        assertThat(DatasetsFinder.findDatasetsObject(new Object[]{"other1", datasets, "other2"})).isEqualTo(datasets);
    }
    
    @Test
    public void shouldCreateDatasetsKey22() {
        Datasets datasets = Datasets.toDetectDataset(1);

        DatasetsKey key1 = new DatasetsKey(datasets, datasets);
        DatasetsKey key2 = new DatasetsKey(datasets);

        assertThat(key1).isEqualTo(key2);
    }
}
