package com.acuity.visualisations.common.cache;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;

@Data
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ClearCacheStatus implements Serializable {

    private List<String> clearedCacheNames = new ArrayList<>();
    private List<String> retainedCacheNames = new ArrayList<>();

    public ClearCacheStatus add(ClearCacheStatus otherStatus) {
        ClearCacheStatus newCacheStatus = new ClearCacheStatus();

        List<String> allClearedCaches = Stream.concat(otherStatus.clearedCacheNames.stream(), clearedCacheNames.stream())
                .distinct().sorted().collect(toList());

        List<String> allRetainedCacheNames1 = newArrayList();
        allRetainedCacheNames1.addAll(retainedCacheNames);
        allRetainedCacheNames1.removeAll(otherStatus.clearedCacheNames);

        List<String> allRetainedCacheNames2 = newArrayList();
        allRetainedCacheNames2.addAll(otherStatus.retainedCacheNames);
        allRetainedCacheNames2.removeAll(clearedCacheNames);

        List<String> allRetainedCacheNames = Stream.concat(allRetainedCacheNames1.stream(), allRetainedCacheNames2.stream())
                .distinct().sorted().collect(toList());

        newCacheStatus.clearedCacheNames = allClearedCaches;
        newCacheStatus.retainedCacheNames = allRetainedCacheNames;

        return newCacheStatus;
    }

    public void addClearedCacheNames(List<String> otherClearedCacheNames) {
        List<String> allClearedCaches = Stream.concat(otherClearedCacheNames.stream(), clearedCacheNames.stream())
                .distinct().sorted().collect(toList());

        clearedCacheNames = allClearedCaches;
    }
}
