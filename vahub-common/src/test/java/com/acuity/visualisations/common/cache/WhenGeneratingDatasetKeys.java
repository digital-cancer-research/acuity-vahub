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
