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
