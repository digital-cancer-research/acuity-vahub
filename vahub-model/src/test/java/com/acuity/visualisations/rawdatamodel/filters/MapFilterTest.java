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

package com.acuity.visualisations.rawdatamodel.filters;

import com.acuity.visualisations.rawdatamodel.util.AlphanumComparator;
import static com.google.common.collect.Lists.newArrayList;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.collect.Sets.newHashSet;
import java.util.Set;
import java.util.TreeSet;
import static org.assertj.core.api.Assertions.assertThat;

public class MapFilterTest {

    @Test
    public void testCanBeHidden() {
        MapFilter<String, SetFilter<String>> mapFilter = new MapFilter<>(SetFilter.class);
        
        assertThat(mapFilter.canBeHidden()).isTrue();         
    }
    
    @Test
    public void testCanBeHiddenNulls() {
        MapFilter<String, SetFilter<String>> mapFilter = new MapFilter<>(SetFilter.class);
               
        Map<String, SetFilter<String>> innerFilters = new HashMap<>();        
        Set<String> values = new TreeSet<>(new AlphanumComparator<String>());
        values.add(null);
        SetFilter<String> setFilter1 = new SetFilter<String>(values, true);
        innerFilters.put("key1", setFilter1);
        mapFilter.setMap(innerFilters);
        
        assertThat(mapFilter.canBeHidden()).isTrue();
    }
    
    @Test
    public void testCanBeHiddenValid() {
        MapFilter<String, SetFilter<String>> mapFilter = new MapFilter<>(SetFilter.class);
               
        Map<String, SetFilter<String>> innerFilters = new HashMap<>();        
        SetFilter<String> setFilter1 = new SetFilter<String>(newArrayList("d"));
        innerFilters.put("key1", setFilter1);
        mapFilter.setMap(innerFilters);
        
        assertThat(mapFilter.canBeHidden()).isFalse();
    }
    
    @Test
    public void testSetMap() {
        MapFilter<String, SetFilter<String>> mapFilter = new MapFilter<>(SetFilter.class);

        Map<String, SetFilter<String>> innerFilters = new HashMap<>();
        SetFilter<String> setFilter1 = new SetFilter<>(newHashSet("Yes", "No"));
        innerFilters.put("key1", setFilter1);

        mapFilter.setMap(innerFilters);
        
        assertThat(mapFilter.getMap()).containsKeys("key1");
        assertThat(mapFilter.getMap().get("key1").getValues()).containsExactlyInAnyOrder("Yes", "No");
    }

    @Test
    public void testIsValid() {
        MapFilter<String, SetFilter<String>> mapFilter = new MapFilter<>(SetFilter.class);
        assertThat(mapFilter.isValid()).isFalse();
        
        mapFilter.setMap(new HashMap<>());
        assertThat(mapFilter.isValid()).isFalse();
        
        Map<String, SetFilter<String>> innerFilters = new HashMap<>();
        innerFilters.put("key1", new SetFilter<>(newHashSet("val1", "val2")));
        mapFilter.setMap(innerFilters);        
        assertThat(mapFilter.isValid()).isTrue();
        
        innerFilters.put("key1", new SetFilter<>());
        mapFilter.setMap(innerFilters);
        assertThat(mapFilter.isValid()).isFalse();
        
        innerFilters.get("key1").setIncludeEmptyValues(true);
        assertThat(mapFilter.isValid()).isTrue();
        
        mapFilter.setMap(new HashMap<>());
        assertThat(mapFilter.isValid()).isFalse();
    }

    @Test
    public void testCompleteWithValue() {

        MapFilter<String, SetFilter<String>> mapFilter = new MapFilter<>(SetFilter.class);
        Map<String, String> map1 = new HashMap<>();
        map1.put("drug1", "Y");
        mapFilter.completeWithValue(map1);
        
        assertThat(mapFilter.getMap().keySet()).containsExactly("drug1");
        assertThat(mapFilter.getMap().get("drug1").getValues()).containsExactly("Y");

        Map<String, String> map2 = new HashMap<>();
        map2.put("drug1", "N");
        mapFilter.completeWithValue(map2);
        
        assertThat(mapFilter.getMap().keySet()).containsExactly("drug1");
        assertThat(mapFilter.getMap().get("drug1").getValues()).containsExactlyInAnyOrder("Y", "N");

        Map<String, String> map3 = new HashMap<>();
        map3.put("drug2", "N");
        mapFilter.completeWithValue(map3);
        
        assertThat(mapFilter.getMap().keySet()).containsExactlyInAnyOrder("drug1", "drug2");
        assertThat(mapFilter.getMap().get("drug1").getValues()).containsExactlyInAnyOrder("Y", "N");
        assertThat(mapFilter.getMap().get("drug2").getValues()).containsExactlyInAnyOrder("N");
    }

    @Test
    public void testComplete() {

        MapFilter<String, SetFilter<String>> mapFilter1 = new MapFilter<>(SetFilter.class);

        SetFilter<String> setFilter1 = new SetFilter<>(newHashSet("10 mg", "30 mg"));
        SetFilter<String> setFilter2 = new SetFilter<>(newHashSet("10 mg", "40 mg", "50 mg"));
        Map<String, SetFilter<String>> map1 = new HashMap<>();
        map1.put("drug1", setFilter1);
        map1.put("drug2", setFilter2);
        MapFilter<String, SetFilter<String>> mapFilter2 = new MapFilter<>(map1, SetFilter.class);
        mapFilter1.complete(mapFilter2);
        
        assertThat(mapFilter1.getMap().keySet()).containsExactlyInAnyOrder("drug1", "drug2");
        assertThat(mapFilter1.getMap().get("drug1").getValues()).containsExactlyInAnyOrder("10 mg", "30 mg");
        assertThat(mapFilter1.getMap().get("drug2").getValues()).containsExactlyInAnyOrder("10 mg", "40 mg", "50 mg");

        SetFilter<String> setFilter3 = new SetFilter<>(newHashSet("10 mg", "80 mg"));
        Map<String, SetFilter<String>> map2 = new HashMap<>();
        map2.put("drug1", setFilter3);
        MapFilter<String, SetFilter<String>> mapFilter3 = new MapFilter<>(map2, SetFilter.class);
        mapFilter1.complete(mapFilter3);
        
        assertThat(mapFilter1.getMap().keySet()).containsExactlyInAnyOrder("drug1", "drug2");
        assertThat(mapFilter1.getMap().get("drug1").getValues()).containsExactlyInAnyOrder("10 mg", "30 mg", "80 mg");
        assertThat(mapFilter1.getMap().get("drug2").getValues()).containsExactlyInAnyOrder("10 mg", "40 mg", "50 mg");
    }
}
