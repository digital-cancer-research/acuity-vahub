package com.acuity.visualisations.rawdatamodel.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ObjectUtilMapWrapperTest {

    @Test
    public void shouldWorkWithHashSet() {

        Map<Integer, Object> mapA = new HashMap<>();
        mapA.put(1, "One");
        mapA.put(2, "Two");
        mapA.put(3, "Three");

        Map<Integer, Object> mapB = new HashMap<>();
        mapB.put(1, "One");
        mapB.put(2, "Two");
        mapB.put(3, "Three");

        Map<Integer, Object> mapC = new HashMap<>();
        mapC.put(1, "One");
        mapC.put(2, "Two");
        mapC.put(3, "ThreeX");

        ObjectUtil.MapWrapper<Integer> mapAWrapped = new ObjectUtil.MapWrapper<>(mapA);
        ObjectUtil.MapWrapper<Integer> mapBWrapped = new ObjectUtil.MapWrapper<>(mapB);
        ObjectUtil.MapWrapper<Integer> mapCWrapped = new ObjectUtil.MapWrapper<>(mapC);

        Set<ObjectUtil.MapWrapper<Integer>> set = new HashSet<>();
        set.add(mapAWrapped);

        Assert.assertTrue(set.contains(mapAWrapped));
        Assert.assertTrue(set.contains(mapBWrapped));
        Assert.assertFalse(set.contains(mapCWrapped));
    }
}
