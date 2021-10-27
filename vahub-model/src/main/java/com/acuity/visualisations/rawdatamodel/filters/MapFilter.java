package com.acuity.visualisations.rawdatamodel.filters;

import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

@EqualsAndHashCode(of = "map")
@ToString
@NoArgsConstructor
public class MapFilter<T extends Comparable<T>, F extends Filter<T>> implements Serializable, HideableFilter {

    private static final Ordering<String> ORDERED_NULLS_LAST = Ordering.natural().nullsLast();

    private boolean valid = false;

    @Getter
    private Map<String, F> map = new TreeMap<>(ORDERED_NULLS_LAST);

    @Getter
    private Class<F> filterClass;

    public void setMap(Map<String, F> map) {
        this.map = new TreeMap<>(ORDERED_NULLS_LAST);
        this.map.putAll(map);
    }

    public MapFilter(Class filterClass) {
        this.map = new TreeMap<>(ORDERED_NULLS_LAST);
        this.filterClass = filterClass;
    }

    public MapFilter(Map<String, F> map, Class filterClass) {
        this(filterClass);
        this.map.putAll(map);
    }

    public boolean containsOnly(String key, F value) {
        return map.containsKey(key) && map.get(key).equals(value) && map.size() == 1;
    }

    public boolean isValid() {
        return valid = (map != null && !map.isEmpty() && map.values().stream().allMatch(Filter::isValid));
    }

    public void complete(MapFilter<T, F> mapFilter) {
        mapFilter.getMap().forEach(this::completeWithValue);
    }

    private void completeWithValue(String key, T value) {

        if (!map.containsKey(key)) {
            F instance = (F) Filter.newFilter(filterClass);
            map.put(key, instance);
        }
        map.get(key).completeWithValue(value);
    }

    public void completeWithValue(Map<String, T> otherMap) {
        otherMap.forEach(this::completeWithValue);
    }
    
    public void completeWithValue(Multimap<String, T> otherMap) {
        otherMap.keySet().stream().forEach((String k) -> {
            Collection<T> list = otherMap.get(k);
            list.forEach(i -> this.completeWithValue(k, i));
        });
    }

    private void completeWithValue(String key, F filter) {
        if (!map.containsKey(key)) {
            map.put(key, filter);
        } else {
            map.get(key).complete(filter);
        }
    }

    @Override
    public boolean canBeHidden() {

        return getMap().keySet().isEmpty()
                || getMap().values().stream().
                        filter(f -> f instanceof HideableFilter).
                        map(f -> (HideableFilter) f).
                        allMatch(h -> h.canBeHidden());
    }
}
