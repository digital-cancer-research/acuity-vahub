package com.acuity.visualisations.common.cache;

import org.junit.Test;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

public class WhenMergingCaches {
    @Test
    public void shouldMergeCaches() {
        ClearCacheStatus s1 = new ClearCacheStatus();
        s1.setClearedCacheNames(newArrayList("C1", "C2"));
        s1.setRetainedCacheNames(newArrayList("C3", "C4"));

        ClearCacheStatus s2 = new ClearCacheStatus();
        s2.setClearedCacheNames(newArrayList("C3"));
        s2.setRetainedCacheNames(newArrayList("C1"));

        ClearCacheStatus add = s1.add(s2);
        ClearCacheStatus add1 = s2.add(s1);

        assertThat(add.getClearedCacheNames()).containsSequence("C1", "C2", "C3");
        assertThat(add1.getClearedCacheNames()).containsSequence("C1", "C2", "C3");

        assertThat(add.getRetainedCacheNames()).containsSequence("C4");
        assertThat(add1.getRetainedCacheNames()).containsSequence("C4");
    }

    @Test
    public void shouldMergeCaches2() {
        ClearCacheStatus s1 = new ClearCacheStatus();
        s1.setClearedCacheNames(newArrayList("C1", "C2", "C5"));
        s1.setRetainedCacheNames(newArrayList("C3", "C4", "C7"));

        ClearCacheStatus s2 = new ClearCacheStatus();
        s2.setClearedCacheNames(newArrayList("C3", "C7"));
        s2.setRetainedCacheNames(newArrayList("C1", "C2"));

        ClearCacheStatus add = s1.add(s2);
        ClearCacheStatus add1 = s2.add(s1);

        assertThat(add.getClearedCacheNames()).containsSequence("C1", "C2", "C3", "C5", "C7");
        assertThat(add1.getClearedCacheNames()).containsSequence("C1", "C2", "C3", "C5", "C7");

        assertThat(add.getRetainedCacheNames()).containsSequence("C4");
        assertThat(add1.getRetainedCacheNames()).containsSequence("C4");
    }
}
