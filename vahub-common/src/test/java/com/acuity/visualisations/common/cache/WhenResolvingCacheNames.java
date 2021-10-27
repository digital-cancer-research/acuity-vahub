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

import com.acuity.visualisations.common.config.Constants;
import static com.acuity.visualisations.common.config.Constants.DETECT_PERSISTENT_CACHE;
import com.acuity.va.security.acl.domain.Datasets;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

/**
 *
 * @author ksnd199
 */
public class WhenResolvingCacheNames {

    private RefreshableCacheResolver refreshableCacheResolver = new RefreshableCacheResolver();
   
    @Test
    public void shouldResolveSingleDatasets() {
        String resolveCacheName = refreshableCacheResolver.resolveCacheName(Datasets.toDetectDataset(1L), "simpleClassName", "methodName");
        
        assertThat(resolveCacheName).isEqualTo(DETECT_PERSISTENT_CACHE + "simpleClassName.methodName");
    }
    
    @Test
    public void shouldResolveSingleAcuityDatasets() {
        String resolveCacheName = refreshableCacheResolver.resolveCacheName(Datasets.toAcuityDataset(1L), "simpleClassName", "methodName");
        
        assertThat(resolveCacheName).isEqualTo(Constants.ACUITY_DAILY_REFRESHABLE_CACHE + "simpleClassName.methodName");
    }
    
    @Test
    public void shouldResolveMultipleDatasets() {
        String resolveCacheName = refreshableCacheResolver.resolveCacheName(Datasets.toDetectDataset(1L, 2L), "simpleClassName", "methodName");
        
        assertThat(resolveCacheName).isEqualTo(DETECT_PERSISTENT_CACHE + "simpleClassName.methodName");
    }
    
    @Test
    public void shouldResolveMultipleDatasetsWithOrder() {
        String resolveCacheName = refreshableCacheResolver.resolveCacheName(Datasets.toDetectDataset(2L, 1L), "simpleClassName", "methodName");
        
        assertThat(resolveCacheName).isEqualTo(DETECT_PERSISTENT_CACHE + "simpleClassName.methodName");
    }

}
