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
