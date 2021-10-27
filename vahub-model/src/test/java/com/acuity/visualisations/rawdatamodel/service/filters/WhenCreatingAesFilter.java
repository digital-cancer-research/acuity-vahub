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

package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.AeFilters;
import com.acuity.visualisations.rawdatamodel.vo.AeDetailLevel;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;

public class WhenCreatingAesFilter {

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldReturnAeDetailChecksOnConstruction() {
        AeFilters aesFilters = new AeFilters();
        
        softly.assertThat(aesFilters.isAePerIncidence()).isTrue();
        softly.assertThat(aesFilters.isAePerSeverityChange()).isFalse();
    }

    @Test
    public void shouldReturnAeDetailChecksOnIsAePerIncidence() {
        AeFilters aesFilters = new AeFilters();
        aesFilters.setAeDetailLevel(AeDetailLevel.PER_INCIDENCE);
        
        softly.assertThat(aesFilters.isAePerIncidence()).isTrue();
        softly.assertThat(aesFilters.isAePerSeverityChange()).isFalse();
    }

    @Test
    public void shouldReturnAeDetailChecksOnIsAePerIncidenceIfBothReturned() {
        AeFilters aesFilters = new AeFilters();
        aesFilters.setAeDetailLevel(AeDetailLevel.PER_INCIDENCE);
        
        softly.assertThat(aesFilters.isAePerIncidence()).isTrue();
        softly.assertThat(aesFilters.isAePerSeverityChange()).isFalse();
    }

    @Test
    public void shouldReturnAeDetailChecksOnIsAePerSeverityChange() {
        AeFilters aesFilters = new AeFilters();
        aesFilters.setAeDetailLevel(AeDetailLevel.PER_SEVERITY_CHANGE);
        
        softly.assertThat(aesFilters.isAePerIncidence()).isFalse();
        softly.assertThat(aesFilters.isAePerSeverityChange()).isTrue();
    }
}
