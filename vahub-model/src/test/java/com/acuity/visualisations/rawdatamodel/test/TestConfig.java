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

package com.acuity.visualisations.rawdatamodel.test;

import com.acuity.visualisations.common.lookup.BeanLookupService;
import com.acuity.visualisations.common.study.metadata.ModuleMetadata;
import com.acuity.visualisations.rawdatamodel.dataproviders.common.DataProviderCacheResolver;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

/**
 *
 * @author ksnd199
 */
@ContextConfiguration
@ComponentScan(
        lazyInit = true,
        basePackages = {"com.acuity.visualisations.rawdatamodel"},
        excludeFilters = {
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {Repository.class}),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {ModuleMetadata.class}),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.acuity.visualisations.rawdatamodel.filters.compatibility..*"),
                //@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {DatasetsDataProvider.class})
        }
)
@ActiveProfiles(profiles = {"it"})
public class TestConfig {

    @MockBean
    protected BeanLookupService lkup;

    @MockBean
    protected DataProviderCacheResolver dataProviderCacheResolver;
}
