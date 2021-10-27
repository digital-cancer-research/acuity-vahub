package com.acuity.visualisations.rawdatamodel.util;

import com.acuity.visualisations.rawdatamodel.vo.CategoryValue;
import com.google.common.base.Splitter;
import static com.google.common.collect.Lists.newArrayList;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author ksnd199
 */
public final class SplitterUtil {

    private SplitterUtil() {
    }

    public static List<CategoryValue> split(String value) {
        if (value == null) {
            return newArrayList();
        }

        return Splitter.on(",").omitEmptyStrings().trimResults().splitToList(value).stream().
                map(i -> {
                    List<String> categories = Splitter.on(":").omitEmptyStrings().trimResults().splitToList(i);
                    return new CategoryValue(categories.get(0), categories.get(1));
                }).collect(toList());
    }
    
    public static Map<String, String> splitAsMap(String value) {
         if (value == null) {
            return Maps.newHashMap();
        }

        return Splitter.on(",").omitEmptyStrings().trimResults().splitToList(value).stream().
                map(i -> {
                    List<String> categories = Splitter.on(":").omitEmptyStrings().trimResults().splitToList(i);
                    return new ImmutablePair<>(categories.get(0), categories.get(1));
                }).collect(Collectors.toMap(Pair::getLeft,  Pair::getRight));        
    }
}
