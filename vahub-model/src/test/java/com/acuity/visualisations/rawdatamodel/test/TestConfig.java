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
