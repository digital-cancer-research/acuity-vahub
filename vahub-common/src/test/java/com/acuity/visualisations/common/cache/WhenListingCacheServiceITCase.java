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

import com.acuity.va.security.acl.domain.Datasets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.List;

import static com.acuity.visualisations.common.cache.DatasetsKeyGenerator.generateDatasetsKey;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(net.sf.ehcache.Cache.class)
@PowerMockIgnore("org.apache.log4j.*")
public class WhenListingCacheServiceITCase {
    @InjectMocks
    private RefreshCacheService refreshCacheService;
    @Mock
    private CacheManager cacheManager;

    private Datasets singleDetectDatasets = Datasets.toDetectDataset(-12L);
    private Datasets singleAcuityDatasets = Datasets.toAcuityDataset(-22L);;
       
    @Test
    public void shouldListPrimedCaches() {

        Cache mockCache = PowerMockito.mock(Cache.class);
        net.sf.ehcache.Cache mockEhCache = PowerMockito.mock(net.sf.ehcache.Cache.class);

        when(cacheManager.getCache(any())).thenReturn(mockCache);
        doReturn(mockEhCache).when(mockCache).getNativeCache();
        List<DatasetsKey> list = newArrayList(generateDatasetsKey(singleDetectDatasets), generateDatasetsKey(singleAcuityDatasets));
        PowerMockito.doReturn(list).when(mockEhCache).getKeys();
        
        List<Datasets> primed = refreshCacheService.listPrimedCachedDatasets();
        
        assertThat(primed).contains(singleDetectDatasets, singleAcuityDatasets);
    }
    
    @Test
    public void shouldListPrimedCaches2() {

        Cache mockCache = PowerMockito.mock(Cache.class);
        net.sf.ehcache.Cache mockEhCache = PowerMockito.mock(net.sf.ehcache.Cache.class);

        when(cacheManager.getCache(any())).thenReturn(mockCache);
        doReturn(mockEhCache).when(mockCache).getNativeCache();
        List<Object> list = newArrayList("string", generateDatasetsKey(singleDetectDatasets), generateDatasetsKey(singleAcuityDatasets));
        PowerMockito.doReturn(list).when(mockEhCache).getKeys();
        
        List<Datasets> primed = refreshCacheService.listPrimedCachedDatasets();
        
        assertThat(primed).contains(singleDetectDatasets, singleAcuityDatasets);
        System.out.println(primed);
    }
}
