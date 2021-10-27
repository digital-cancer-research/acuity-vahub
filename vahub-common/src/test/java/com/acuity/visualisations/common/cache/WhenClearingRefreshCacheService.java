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

import com.acuity.visualisations.common.lookup.CacheableDataProvider;
import com.acuity.va.security.acl.domain.Datasets;
import org.junit.Before;
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
import static com.acuity.visualisations.common.config.Constants.DETECT_PERSISTENT_CACHE;
import static com.acuity.visualisations.common.config.Constants.ACUITY_DAILY_REFRESHABLE_CACHE;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(net.sf.ehcache.Cache.class)
@PowerMockIgnore("org.apache.log4j.*")
public class WhenClearingRefreshCacheService {
    @InjectMocks
    private RefreshCacheService refreshCacheService;
    @Mock
    private CacheManager cacheManager;
    @Mock
    private CacheableDataProvider dataProvider;

    private List<String> cacheNames = newArrayList();

    private Datasets singleDetectDatasets;
    private Datasets singleAcuityDatasets;

    @Before
    public void setup() {

        //  set up dummy disk caches
        singleDetectDatasets = Datasets.toDetectDataset(-12L);
        singleAcuityDatasets = Datasets.toAcuityDataset(-22L);

        for (Datasets datasets : newArrayList(singleDetectDatasets, singleAcuityDatasets)) {
            cacheNames.add(RefreshableCacheResolver.resolveCacheName(datasets, "simpleClassName", "methodName"));
            cacheNames.add(RefreshableCacheResolver.resolveCacheName(datasets, "simpleClassName1", "methodName1"));
            cacheNames.add(RefreshableCacheResolver.resolveCacheName(datasets, "simpleClassName2", "methodName2"));
            cacheNames.add(RefreshableCacheResolver.resolveCacheName(datasets, "simpleClassName3", "methodName3"));
        }

        cacheNames.add("Other-cache");
        cacheNames.add("Other-cache1");

        when(cacheManager.getCacheNames()).thenReturn(cacheNames);
    }

    @Test
    public void shouldClearAllCaches() {

        Cache mockCache = mock(Cache.class);
        when(cacheManager.getCache(any())).thenReturn(mockCache);

        ClearCacheStatus clearAllCaches = refreshCacheService.clearAllCaches();

        assertThat(clearAllCaches.getClearedCacheNames()).hasSize(4);
        assertThat(clearAllCaches.getRetainedCacheNames()).hasSize(6);
        verify(mockCache, times(4)).clear();
        verifyNoMoreInteractions(mockCache);
    }

    @Test
    public void shouldClearAllDetectCaches() {

        Cache mockCache = mock(Cache.class);
        when(cacheManager.getCache(any())).thenReturn(mockCache);

        ClearCacheStatus clearAllCaches = refreshCacheService.clearDetectCaches();

        assertThat(clearAllCaches.getClearedCacheNames()).hasSize(4);
        assertThat(clearAllCaches.getClearedCacheNames().get(0)).startsWith(DETECT_PERSISTENT_CACHE);
        assertThat(clearAllCaches.getRetainedCacheNames()).hasSize(6);
        verify(mockCache, times(4)).clear();
        verifyNoMoreInteractions(mockCache);
    }

    @Test
    public void shouldClearAllAcuityCaches() {

        Cache mockCache = mock(Cache.class);
        when(cacheManager.getCache(any())).thenReturn(mockCache);

        ClearCacheStatus clearAllCaches = refreshCacheService.clearAcuityCaches();

        assertThat(clearAllCaches.getClearedCacheNames()).hasSize(4);
        assertThat(clearAllCaches.getClearedCacheNames().get(0)).startsWith(ACUITY_DAILY_REFRESHABLE_CACHE);
        assertThat(clearAllCaches.getRetainedCacheNames()).hasSize(6);
        verify(mockCache, times(4)).clear();
        verifyNoMoreInteractions(mockCache);
    }

    @Test
    public void shouldClearAcuityCachesForAcuity() {

        Cache mockCache = PowerMockito.mock(Cache.class);
        net.sf.ehcache.Cache mockEhCache = PowerMockito.mock(net.sf.ehcache.Cache.class);

        when(cacheManager.getCache(any())).thenReturn(mockCache);
        doReturn(mockEhCache).when(mockCache).getNativeCache();
        List<DatasetsKey> list = newArrayList(generateDatasetsKey(singleDetectDatasets), generateDatasetsKey(singleAcuityDatasets));
        PowerMockito.doReturn(list).when(mockEhCache).getKeys();

        ClearCacheStatus clearAllCaches = refreshCacheService.clearCachesForDatasets(singleAcuityDatasets);

        assertThat(clearAllCaches.getClearedCacheNames()).hasSize(4);
        assertThat(clearAllCaches.getClearedCacheNames().get(0)).startsWith(ACUITY_DAILY_REFRESHABLE_CACHE);
        assertThat(clearAllCaches.getRetainedCacheNames()).hasSize(6);
        verify(mockCache, times(0)).clear();
        verify(mockEhCache, times(4)).getKeys();
        verify(mockEhCache, times(4)).remove(any(DatasetsKey.class));
    }

    @Test
    public void shouldClearDetectCachesForDetect() {

        Cache mockCache = PowerMockito.mock(Cache.class);
        net.sf.ehcache.Cache mockEhCache = PowerMockito.mock(net.sf.ehcache.Cache.class);

        when(cacheManager.getCache(any())).thenReturn(mockCache);
        doReturn(mockEhCache).when(mockCache).getNativeCache();
        List<DatasetsKey> list = newArrayList(generateDatasetsKey(singleDetectDatasets), generateDatasetsKey(singleAcuityDatasets));
        PowerMockito.doReturn(list).when(mockEhCache).getKeys();

        ClearCacheStatus clearAllCaches = refreshCacheService.clearCachesForDatasets(singleDetectDatasets);

        assertThat(clearAllCaches.getClearedCacheNames()).hasSize(4);
        assertThat(clearAllCaches.getClearedCacheNames().get(0)).startsWith(DETECT_PERSISTENT_CACHE);
        assertThat(clearAllCaches.getRetainedCacheNames()).hasSize(6);

        verify(mockCache, times(0)).clear();
        verify(mockEhCache, times(4)).getKeys();
        verify(mockEhCache, times(4)).remove(any(DatasetsKey.class));
        //verifyNoMoreInteractions(mockCache);
    }

    @Test
    public void shouldClearDetectCachesForMultiDetect() {

        Datasets multiDetectDatasets = Datasets.toDetectDataset(-12L, -101L);
        cacheNames.add(RefreshableCacheResolver.resolveCacheName(multiDetectDatasets, "simpleClassName5",
                "methodName5"));
        when(cacheManager.getCacheNames()).thenReturn(cacheNames);

        Cache mockCache = PowerMockito.mock(Cache.class);
        net.sf.ehcache.Cache mockEhCache = PowerMockito.mock(net.sf.ehcache.Cache.class);

        when(cacheManager.getCache(any())).thenReturn(mockCache);
        doReturn(mockEhCache).when(mockCache).getNativeCache();
        List<DatasetsKey> list = newArrayList(generateDatasetsKey(multiDetectDatasets), generateDatasetsKey(singleDetectDatasets),
                generateDatasetsKey(singleAcuityDatasets));
        PowerMockito.doReturn(list).when(mockEhCache).getKeys();

        ClearCacheStatus clearAllCaches = refreshCacheService.clearCachesForDatasets(singleDetectDatasets);

        assertThat(clearAllCaches.getClearedCacheNames()).hasSize(5);
        assertThat(clearAllCaches.getClearedCacheNames().get(0)).startsWith(DETECT_PERSISTENT_CACHE);
        assertThat(clearAllCaches.getRetainedCacheNames()).hasSize(6);

        verify(mockCache, times(0)).clear();
        verify(mockEhCache, times(5)).getKeys();
        verify(mockEhCache, times(10)).remove(any(DatasetsKey.class));
        //verifyNoMoreInteractions(mockCache);
    }

    @Test
    public void shouldClearDetectCachesForMultiDetect2() {

        Datasets multiDetectDatasets1 = Datasets.toDetectDataset(-304L, -12L, -101L);
        Datasets multiDetectDatasets2 = Datasets.toDetectDataset(-102L);
        Datasets multiAcuityDatasets1 = Datasets.toAcuityDataset(-12L);
        Datasets multiDetectDatasets3 = Datasets.toDetectDataset(-107L, -12L);
        cacheNames.add(RefreshableCacheResolver.resolveCacheName(multiDetectDatasets1, "simpleClassName5", "methodName5"));
        when(cacheManager.getCacheNames()).thenReturn(cacheNames);

        Cache mockCache = PowerMockito.mock(Cache.class);
        net.sf.ehcache.Cache mockEhCache = PowerMockito.mock(net.sf.ehcache.Cache.class);

        when(cacheManager.getCache(any())).thenReturn(mockCache);
        doReturn(mockEhCache).when(mockCache).getNativeCache();
        List<DatasetsKey> list = newArrayList(generateDatasetsKey(multiAcuityDatasets1), generateDatasetsKey(multiDetectDatasets3),
                generateDatasetsKey(multiDetectDatasets1), generateDatasetsKey(multiDetectDatasets2),
                generateDatasetsKey(singleDetectDatasets), generateDatasetsKey(singleAcuityDatasets));
        PowerMockito.doReturn(list).when(mockEhCache).getKeys();

        ClearCacheStatus clearAllCaches = refreshCacheService.clearCachesForDatasets(singleDetectDatasets);

        assertThat(clearAllCaches.getClearedCacheNames()).hasSize(5);
        assertThat(clearAllCaches.getClearedCacheNames().get(0)).startsWith(DETECT_PERSISTENT_CACHE);
        assertThat(clearAllCaches.getRetainedCacheNames()).hasSize(6);

        verify(mockCache, times(0)).clear();
        verify(mockEhCache, times(5)).getKeys();
        verify(mockEhCache, times(15)).remove(any(DatasetsKey.class));
        //verifyNoMoreInteractions(mockCache);
    }
}
