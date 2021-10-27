package com.acuity.visualisations.rawdatamodel.dataproviders.common;

import com.acuity.visualisations.common.lookup.CacheableDataProvider;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class DataProviderAwareTest {

    @Autowired
    protected CacheableDataProvider dataProvider;

    @Before
    @After
    public void cleanup() {
        dataProvider.clearAllCacheFiles();
    }

}
