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

package com.acuity.visualisations.config.cache;

import com.acuity.visualisations.cache.DiskCacheService;
import com.acuity.visualisations.config.ApplicationEhCacheConfig;
import com.acuity.visualisations.config.ApplicationEnableExecutorConfig;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import static com.acuity.visualisations.cache.DiskCacheService.CACHETYPE.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationEhCacheConfig.class, ApplicationEnableExecutorConfig.class})
public class WhenRunningDiskCacheServicesITCase {
    @Autowired
    private DiskCacheService diskCacheService;
    @Autowired
    private CacheManager cacheManager;
    
    @Test
    public void shouldGetDiskPath() {
        assertThat(diskCacheService.getDiskLocation()).isNotEmpty().contains("cacheStorage");
    }

    @Test
    public void shouldAllListCaches() {
        Collection<String> cacheNames = cacheManager.getCacheNames();

        assertThat(cacheNames).contains("AcuitySecurityResourceClient-hasPermissionForUser", "AcuitySecurityResourceClient-getPermissionForUser");
    }

    @Test
    @Ignore("Method listDiskCaches is deprecated. Test fails on Automation Pipeline (GitHub actions or Jenkins)")
    public void shouldListDiskFiles() throws IOException {
        List<Path> listAllDiskCaches = diskCacheService.listDiskCaches(ALL);
        listAllDiskCaches.forEach(System.out::println);

        assertThat(listAllDiskCaches).isNotEmpty().allMatch(p -> !p.getFileName().toString().equals(DiskCacheService.LOCKFILE_NAME));
        assertThat(listAllDiskCaches).allMatch(p -> p.getFileName().toString().endsWith(".data"));
    }

    @Test
    public void shouldListAcuityDiskFiles() throws IOException {
        List<Path> listAcuityDiskCaches = diskCacheService.listDiskCaches(ACUITY);
        listAcuityDiskCaches.forEach(System.out::println);

        assertThat(listAcuityDiskCaches).allMatch(p -> p.getFileName().toString().contains("visualisations"));
        assertThat(listAcuityDiskCaches).allMatch(p -> p.getFileName().toString().endsWith(".data"));
    }

    @Test
    public void shouldListDetectDiskFiles() throws IOException {
        List<Path> listDetectDiskCaches = diskCacheService.listDiskCaches(DETECT);
        listDetectDiskCaches.forEach(System.out::println);

        assertThat(listDetectDiskCaches).allMatch(p -> p.getFileName().toString().contains("detect"));
        assertThat(listDetectDiskCaches).allMatch(p -> p.getFileName().toString().endsWith("data"));
    }

    //@Test
    public void shouldDeleteDetectDiskFiles() throws IOException {
        List<String> caches = diskCacheService.deleteDiskCaches(DETECT);
        
        caches.forEach(System.out::println);
        assertThat(caches).allMatch(c -> c.contains("detect"));
        assertThat(caches).allMatch(c -> !c.endsWith(".data"));
    }
}

